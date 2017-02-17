package com.consol.citrus.admin.model.spring;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
public class SpringContext {

    private String name;
    private String fileName;
    private String source;

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
     * Gets the fileName.
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the fileName.
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the source.
     *
     * @return
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }
}
