/*
 * {{{ header & license
 * Copyright (c) 2004, 2005 Torbjoern Gannholm
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
 * }}}
 */
package org.xhtmlrenderer.context;

import org.jsoup.nodes.Element;
import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.extend.UserInterface;


/**
 * An instance which works together with a w3c DOM tree
 *
 * @author Torbjoern Gannholm
 */
public class StandardAttributeResolver implements AttributeResolver {
    /**
     * Description of the Field
     */
    private final NamespaceHandler nsh;
    /**
     * Description of the Field
     */
    private final UserAgentCallback uac;
    /**
     * Description of the Field
     */
    private final UserInterface ui;

    /**
     * Constructor for the StandardAttributeResolver object
     *
     * @param nsh PARAM
     * @param uac PARAM
     * @param ui  PARAM
     */
    public StandardAttributeResolver(final NamespaceHandler nsh, final UserAgentCallback uac, final UserInterface ui) {
        this.nsh = nsh;
        this.uac = uac;
        this.ui = ui;
    }

    /**
     * Gets the attributeValue attribute of the StandardAttributeResolver object
     *
     * @param e        PARAM
     * @param attrName PARAM
     * @return The attributeValue value
     */
    public String getAttributeValue(final Object e, final String attrName) {
        return nsh.getAttributeValue((Element) e, attrName);
    }
    
    public String getAttributeValue(final Object e, final String namespaceURI, final String attrName) {
        return nsh.getAttributeValue((Element)e, namespaceURI, attrName);
    }

    /**
     * Gets the class attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The class value
     */
    public String getClass(final Object e) {
        return nsh.getClass((Element) e);
    }

    /**
     * Gets the iD attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The iD value
     */
    public String getID(final Object e) {
        return nsh.getID((Element) e);
    }

    public String getNonCssStyling(final Object e) {
        return nsh.getNonCssStyling((Element) e);
    }

    /**
     * Gets the elementStyling attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The elementStyling value
     */
    public String getElementStyling(final Object e) {
        return nsh.getElementStyling((Element) e);
    }

    /**
     * Gets the lang attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The lang value
     */
    public String getLang(final Object e) {
        return nsh.getLang((Element) e);
    }

    /**
     * Gets the link attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The link value
     */
    public boolean isLink(final Object e) {
        return nsh.getLinkUri((Element) e) != null;
    }

    /**
     * Gets the visited attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The visited value
     */
    public boolean isVisited(final Object e) {
        return isLink(e) && uac.isVisited(nsh.getLinkUri((Element) e));
    }

    /**
     * Gets the hover attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The hover value
     */
    public boolean isHover(final Object e) {
        return ui.isHover((Element) e);
    }

    /**
     * Gets the active attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The active value
     */
    public boolean isActive(final Object e) {
        return ui.isActive((Element) e);
    }

    /**
     * Gets the focus attribute of the StandardAttributeResolver object
     *
     * @param e PARAM
     * @return The focus value
     */
    public boolean isFocus(final Object e) {
        return ui.isFocus((Element) e);
    }
}

