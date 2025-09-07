package org.withor.mixins;

import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.withor.agent.Agent;
import org.withor.agent.AgentLogger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MixinTransformer implements ClassFileTransformer {
    private final Map<String, List<InjectionData>> injections = new HashMap<>();
    private final Set<Class<?>> mixins = new HashSet<>();
    private static final Logger logger = AgentLogger.getLogger(MixinTransformer.class);

    @SneakyThrows
    public MixinTransformer(Set<Class<?>> mixins) {
        this.mixins.addAll(mixins);
        for (Class<?> mixin : mixins) {
            Mixin mixinAnnotation = mixin.getAnnotation(Mixin.class);
            if (mixinAnnotation == null) throw new Exception("Mixin annotation is required");

            String targetClassName = mixinAnnotation.value().getName();

            for (Method m : mixin.getDeclaredMethods()) {
                Inject inj = m.getAnnotation(Inject.class);
                if (inj != null) {
                    injections.computeIfAbsent(targetClassName, k -> new ArrayList<>()).add(new InjectionData(mixin, m, inj));
                }
            }
        }
    }

    @SneakyThrows
    public void loaded(Instrumentation inst) {
        for (Class<?> mixin : mixins) {
            Mixin mixinAnnotation = mixin.getAnnotation(Mixin.class);

            String targetClassName = mixinAnnotation.value().getName();
            Class<?> targetClass = Arrays.stream(inst.getAllLoadedClasses())
                    .filter(cl -> inst.isModifiableClass(cl) && cl.getName().equals(targetClassName))
                    .findFirst()
                    .orElse(null);


            if (targetClass != null && inst.isModifiableClass(targetClass)) {
                inst.retransformClasses(targetClass);
            }
        }
    }


    @Override
    public byte[] transform(Module module, ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain pd,
                            byte[] classfileBuffer) {

        String dotted = className.replace("/", ".");
        if (!injections.containsKey(dotted)) return null;

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,
                                             String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                for (InjectionData inj : injections.get(dotted)) {
                    if (!inj.targetMethod.equals(name)) continue;
                    logger.info("Injecting: {}", dotted);
                    return new AdviceAdapter(Opcodes.ASM9, mv, access, name, desc) {
                        @Override
                        protected void onMethodEnter() {
                            if (!inj.at.equals("HEAD")) return;
                            injectCall(mv, inj, access, desc);
                        }

                        @Override
                        protected void onMethodExit(int opcode) {
                            if (!inj.at.equals("TAIL")) return;
                            injectCall(mv, inj, access, desc);
                        }

                        private void injectCall(MethodVisitor mv, InjectionData inj, int access, String desc) {
                            Type returnType = Type.getReturnType(desc);
                            boolean isVoid = returnType.equals(Type.VOID_TYPE);

                            mv.visitTypeInsn(Opcodes.NEW, "org/withor/mixins/CallbackInfo");
                            mv.visitInsn(Opcodes.DUP);
                            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                                    "org/withor/mixins/CallbackInfo",
                                    "<init>",
                                    "()V",
                                    false);

                            int ciVar = newLocal(Type.getType("Lorg/withor/mixins/CallbackInfo;"));
                            mv.visitVarInsn(Opcodes.ASTORE, ciVar);

                            int varIndex = 0;
                            if ((access & Opcodes.ACC_STATIC) == 0) {
                                mv.visitVarInsn(Opcodes.ALOAD, varIndex++);
                            }

                            Type[] args = Type.getArgumentTypes(desc);
                            for (Type t : args) {
                                mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), varIndex);
                                varIndex += t.getSize();
                            }

                            mv.visitVarInsn(Opcodes.ALOAD, ciVar);

                            mv.visitMethodInsn(
                                    Modifier.isStatic(inj.method.getModifiers()) ?
                                            Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL,
                                    inj.mixinClass.getName().replace('.', '/'),
                                    inj.method.getName(),
                                    Type.getMethodDescriptor(inj.method),
                                    false
                            );

                            if (inj.cancellable) {
                                mv.visitVarInsn(Opcodes.ALOAD, ciVar);
                                mv.visitMethodInsn(
                                        Opcodes.INVOKEVIRTUAL,
                                        "org/withor/mixins/CallbackInfo",
                                        "isCancelled",
                                        "()Z",
                                        false
                                );

                                Label lContinue = new Label();
                                mv.visitJumpInsn(Opcodes.IFEQ, lContinue);

                                if (isVoid) {
                                    mv.visitInsn(Opcodes.RETURN);
                                } else {
                                    mv.visitVarInsn(Opcodes.ALOAD, ciVar);
                                    mv.visitMethodInsn(
                                            Opcodes.INVOKEVIRTUAL,
                                            "org/withor/mixins/CallbackInfo",
                                            "getReturnValue",
                                            "()Ljava/lang/Object;",
                                            false
                                    );
                                    Type retType = Type.getReturnType(desc);
                                    injectReturnCast(retType, mv);
                                    mv.visitInsn(retType.getOpcode(Opcodes.IRETURN));
                                }

                                mv.visitLabel(lContinue);
                            }
                        }

                        private void injectReturnCast(Type retType, MethodVisitor mv) {
                            switch (retType.getSort()) {
                                case Type.OBJECT:
                                case Type.ARRAY:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, retType.getInternalName());
                                    break;
                                case Type.INT:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                                    break;
                                case Type.BOOLEAN:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                                    break;
                                case Type.LONG:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                                    break;
                                case Type.FLOAT:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                                    break;
                                case Type.DOUBLE:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
                                    break;
                                case Type.SHORT:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
                                    break;
                                case Type.BYTE:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
                                    break;
                                case Type.CHAR:
                                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
                                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
                                    break;
                            }
                        }
                    };
                }

                return mv;
            }
        };

        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
}