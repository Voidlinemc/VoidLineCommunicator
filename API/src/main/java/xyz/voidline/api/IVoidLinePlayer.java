package xyz.voidline.api;

public interface IVoidLinePlayer {
    Object getHandle();

    /**
     * Blocks module
     * @param module Module name
     */
    void blockModule(String module);
    /**
     * Unblocks all modules
     * Useful for server switching
     */
    void unblockAll();
    /**
     * Sets custom tablist header / footer
     * @param tabList Tablist builder
     */
    void setTabList(TabList tabList);
}