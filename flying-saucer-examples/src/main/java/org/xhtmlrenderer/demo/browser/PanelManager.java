/*
 * PanelManager.java
 * Copyright (c) 2005 Torbjoern Gannholm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */
package org.xhtmlrenderer.demo.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.resource.HTMLResource;
import org.xhtmlrenderer.swing.DelegatingUserAgent;
import org.xhtmlrenderer.util.StreamResource;
import org.xhtmlrenderer.util.Uu;
import org.xhtmlrenderer.util.GeneralUtil;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
 * PanelManager is a UserAgentCallback responsible for the Browser's resource (XML, image, CSS) lookup. Most of the
 * power is in the NaiveUserAgent; the PanelManager adds support for the demo:, file: and demoNav: protocols,
 * and keeps track of the history of visited links. There is always a "current" link, and one can use the
 * {@link #getBack()}, {@link #getForward()} and {@link #hasForward()} methods to navigate within the history.
 * As a NaiveUserAgent, the PanelManager is also a DocumentListener, but must be added to the source of document
 * events (like a RootPanel subclass).
 *  
 */
public class PanelManager extends DelegatingUserAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(PanelManager.class);
    private int index = -1;
    private final ArrayList history = new ArrayList();


    /**
     * {@inheritdoc}.
     */
    public String resolveURI(final String uri) {
        final String burl = getBaseURL();

        URL ref = null;

        if (uri == null) return burl;
        if (uri.trim().equals("")) return burl; //jar URLs don't resolve this right

        if (uri.startsWith("demo:")) {
            final DemoMarker marker = new DemoMarker();
            String short_url = uri.substring(5);
            if (!short_url.startsWith("/")) {
                short_url = "/" + short_url;
            }
            ref = marker.getClass().getResource(short_url);
            Uu.p("ref = " + ref);
        } else if (uri.startsWith("demoNav:")) {
            final DemoMarker marker = new DemoMarker();
            String short_url = uri.substring("demoNav:".length());
            if (!short_url.startsWith("/")) {
                short_url = "/" + short_url;
            }
            ref = marker.getClass().getResource(short_url);
            Uu.p("Demo navigation URI, ref = " + ref);
        } else if (uri.startsWith("javascript")) {
            Uu.p("Javascript URI, ignoring: " + uri);
        } else if (uri.startsWith("news")) {
            Uu.p("News URI, ignoring: " + uri);
        } else {
            try {
                URL base;
                if (burl == null || burl.length() == 0) {
                    base = new File(".").toURL();
                } else {
                    base = new URL(burl);
                }
                ref = new URL(base, uri);
            } catch (final MalformedURLException e) {
                Uu.p("URI/URL is malformed: " + burl + " or " + uri);
            }
        }

        if (ref == null)
            return null;
        else
            return ref.toExternalForm();
    }

	/**
	 * {@inheritDoc}
	 */
	public HTMLResource getXMLResource(String uri) {
        uri = resolveURI(uri);
        if (uri != null && uri.startsWith("file:")) {
            File file = null;
            try {
                final StringBuffer sbURI = GeneralUtil.htmlEscapeSpace(uri);

                LOGGER.info("Encoded URI: " + sbURI);
                file = new File(new URI(sbURI.toString()));
            } catch (final URISyntaxException
                    e) {
                LOGGER.error("Invalid file URI " + uri, e);
                return getNotFoundDocument(uri);
            }
            if (file.isDirectory()) {
                final String dirlist = DirectoryLister.list(file);
                return HTMLResource.load(new StringReader(dirlist));
            }
        }
        HTMLResource xr = null;
        URLConnection uc = null;
        InputStream inputStream = null;
        try {
        	final StreamResource strm = new StreamResource(uri);
        	strm.connect();
        	uc = strm.getUrlConnection();
            final String contentType = uc.getContentType();

            LOGGER.info("Content-Type = " + contentType);

            if (uc instanceof HttpURLConnection)
            {
            	LOGGER.info( "Response Code = " + ((HttpURLConnection) uc).getResponseCode());
            	LOGGER.info( "Response Message = " + ((HttpURLConnection) uc).getResponseMessage());
            }
            
            //Maybe should popup a choice when content/unknown!
            if (contentType == null || contentType.equals("text/plain") || contentType.equals("content/unknown")) {
                inputStream = strm.bufferedStream();
                xr = HTMLResource.load(inputStream, uri);
            } else if (contentType.startsWith("image")) {
                final String doc = "<img src='" + uri + "'/>";
                xr = HTMLResource.load(doc);
            } else {
                inputStream = strm.bufferedStream();
                xr = HTMLResource.load(inputStream, uri);
            }
        } catch (final MalformedURLException e) {
            LOGGER.error("bad URL given: " + uri, e);
        } catch (final IOException e) {
            LOGGER.error("IO problem for " + uri, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    // swallow
                }
            }
        }

        if (xr == null) {
            xr = getNotFoundDocument(uri);
        }
        return xr;
    }

	/**
	 * Used internally when a document can't be loaded--returns XHTML as an XMLResource indicating that fact.
	 *
	 * @param uri The URI which could not be loaded.
	 *
	 * @return An XMLResource containing XML which about the failure.
	 */
	private HTMLResource getNotFoundDocument(final String uri) {
        HTMLResource xr;

        // URI may contain & symbols which can "break" the XHTML we're creating
        final String cleanUri = GeneralUtil.escapeHTML(uri);
        final String notFound = "<html><h1>Document not found</h1><p>Could not access URI <pre>" + cleanUri + "</pre></p></html>";

        xr = HTMLResource.load(new StringReader(notFound));
        return xr;
    }

	/**
	 * Returns true if the link has been visited by the user in this session. Visit tracking is not persisted.
	 */
	public boolean isVisited(String uri) {
        if (uri == null) return false;
        uri = resolveURI(uri);
        return history.contains(uri);
    }

	/**
	 * {@inheritDoc}
	 */
	public void setBaseURL (final String url) {
		String burl = super.getBaseURL();
		if(burl !=null &&  burl.startsWith("error:")) burl = null;
        
        burl = resolveURI(url);
        if (burl == null) burl = "error:FileNotFound";

		super.setBaseURL(burl);

		// setBaseURL is called by view when document is loaded
        if (index >= 0) {
            final String historic = (String) history.get(index);
            if (historic.equals(burl)) return; //moved in history
        }
        index++;
        for (final int i = index; i < history.size(); history.remove(i)) ;
        history.add(index, burl);
    }


	/**
	 * Returns the "next" URI in the history of visiting URIs. Advances the URI tracking (as if browser "forward" was
	 * used).
	 */
	public String getForward() {
        index++;
        return (String) history.get(index);
    }

	/**
	 * Returns the "previous" URI in the history of visiting URIs. Moves the URI tracking back (as if browser "back" was
	 * used).
	 */
	public String getBack() {
        index--;
        return (String) history.get(index);
    }

	/**
	 * Returns true if there are visited URIs in history "after" the pointer the the current URI. This would be the case
	 * if multiple URIs were visited and the getBack() had been called at least once.
	 */
	public boolean hasForward() {
        if (index + 1 < history.size() && index >= 0) {
            return true;
        } else {
            return false;
        }
    }

	/**
	 * Returns true if there are visited URIs in history "before" the pointer the the current URI. This would be the case
	 * if multiple URIs were visited and the current URI pointer was not at the begininnig of the visited URI list. 
	 */
    public boolean hasBack() {
        if (index > 0) {
            return true;
        } else {
            return false;
        }
    }
}
