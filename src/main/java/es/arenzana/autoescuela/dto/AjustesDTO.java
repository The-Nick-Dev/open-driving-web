package es.arenzana.autoescuela.dto;

import es.arenzana.autoescuela.model.Ajustes;

public class AjustesDTO {
    private Long id;
    private String nombreEmpresa;
    private String mailHost;
    private Integer mailPort;
    private String mailUsername;
    private String mailProtocol;

    public AjustesDTO() {}

    public AjustesDTO(Ajustes ajustes) {
        this.id = ajustes.getId();
        this.nombreEmpresa = ajustes.getNombreEmpresa();
        this.mailHost = ajustes.getMailHost();
        this.mailPort = ajustes.getMailPort();
        this.mailUsername = ajustes.getMailUsername();
        this.mailProtocol = ajustes.getMailProtocol();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public Integer getMailPort() {
        return mailPort;
    }

    public void setMailPort(Integer mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    public String getMailProtocol() {
        return mailProtocol;
    }

    public void setMailProtocol(String mailProtocol) {
        this.mailProtocol = mailProtocol;
    }
}
