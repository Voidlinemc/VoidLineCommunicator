### What is SMCWVC

**SMCWVC** (ScriptMessageCommunicatorWithVoidlineClient) is a communication protocol for client-to-server and server-to-client. It is based on the [ScriptMessagePacket (ID 177)](https://mojang.github.io/bedrock-protocol-docs/html/ScriptMessagePacket.html).

---

| ID                     | Data               | Description                                                                 |
|------------------------|--------------------|-----------------------------------------------------------------------------|
| `voidline:handshake`   | `smcwvcVersion`    | Handshake packet. Sends every time player joins server / injects the client |
| `voidline:blockmodule` | `module`           | Packet to block module for current server                                   |
| `voidline:unblockall`  |                    | Removes block from all blocked modules                                      |
| `voidline:tablist`     | `header`, `footer` | Sets tablist footer and header                                              |
