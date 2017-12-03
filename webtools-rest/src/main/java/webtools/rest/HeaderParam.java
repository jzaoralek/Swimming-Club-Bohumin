package webtools.rest;

/**
 * Project: jira-client
 *
 * Created: 15. 10. 2016
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class HeaderParam {

    private String name;
    private String value;

    public HeaderParam() {
    }

    public HeaderParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
