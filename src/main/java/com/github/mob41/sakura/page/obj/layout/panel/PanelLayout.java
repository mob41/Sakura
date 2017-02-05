package com.github.mob41.sakura.page.obj.layout.panel;

public class PanelLayout {

	private final PanelHeading heading;
	
	private final PanelBody body;
	
	private final PanelFooter footer;
	
	public PanelLayout(String heading, String body, String footer){
		this(new PanelHeading(heading), new PanelBody(body), new PanelFooter(footer));
	}
	
	public PanelLayout(PanelHeading heading, PanelBody body, PanelFooter footer) {
		this.heading = heading;
		this.body = body;
		this.footer = footer;
	}
	
	public PanelHeading getHeading(){
		return heading;
	}
	
	public PanelBody getBody(){
		return body;
	}
	
	public PanelFooter getFooter(){
		return footer;
	}
	
	public PanelLayoutObject[] getAll(){
		return new PanelLayoutObject[]{heading, body, footer};
	}

}
