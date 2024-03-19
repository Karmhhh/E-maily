package com.example.Emaily.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class EmailObj {

    @Id
    private UUID emailId;
    private String mittente;
    private String destinatario;
    private String emailObject;
    private String emailBody;
    private List<String> ccs = new ArrayList<>();
    private String dateSend;
    private boolean state;

    public UUID getEmailId() {
        return emailId;
    }

    public void setEmailId(UUID emailId) {
        if (emailId == null) {
            this.emailId = UUID.randomUUID();
        } else {
            this.emailId = emailId;
        }

    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getEmailObject() {
        return emailObject;
    }

    public void setEmailObject(String emailObject) {
        this.emailObject = emailObject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public List<String> getCCs() {
        return ccs;
    }

    public void setCCs(List<String> ccs) {
        this.ccs = ccs;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dataString) {
        if (dataString == null) {

            this.dateSend = LocalDateTime.now().toString();
        } else {
            this.dateSend = dataString;
        }
    }

    public Integer isState() {
        return state ? 1 : 0;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "EmailObj {emailId:" + emailId + ", mittente:" + mittente + ", destinatario:" + destinatario
                + ", emailObject:" + emailObject + ", emailBody:" + emailBody + ", ccs:" + ccs + ", dateSend:"
                + dateSend + ", state:" + state + "}";
    }

}
