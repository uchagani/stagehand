package io.github.uchagani.stagehand.tests;

public class HTMLConstants {
    public static final String SIMPLE_HTML = "<!doctypehtml><h2 id=headerId>Header Text</h2><p class=paragraph>This is a paragraph.<div id=firstNameFormDiv><form><input placeholder=\"First Name\"></form></div><div id=lastNameFormDiv><form><input placeholder=\"Last Name\"></form><div id=cityDiv><input placeholder=City></div></div>";
    public static final String IFRAME_HTML = "<!DOCTYPE html><iframe height=500 width=800 id=parentIframe srcdoc=\"<p id=iframe-paragraph>Hello from inside an iframe!</p><iframe id=childIframe height=500 width=800 srcdoc='<p id=child-iframe-paragraph>Child iFrame paragraph</p>' </iframe>\"></iframe>";
}
