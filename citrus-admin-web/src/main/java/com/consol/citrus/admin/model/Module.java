package com.consol.citrus.admin.model;

/**
 * @author Christoph Deppisch
 */
public class Module {

    private String name;
    private String version;

    private boolean active;

    /**
     * Default constructor.
     */
    public Module() {
        super();
    }

    /**
     * Default constructor using fields.
     * @param name
     * @param version
     * @param active
     */
    public Module(String name, String version, boolean active) {
        this.name = name;
        this.version = version;
        this.active = active;
    }

    /**
     * Gets the name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the version.
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the active.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
