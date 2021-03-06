/*
 * {{{ header & license
 * Copyright (c) 2007 Sean Bright
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;

class SubmitField extends AbstractButtonField {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitField.class);

    public SubmitField(final Element e, final XhtmlForm form, final LayoutContext context, final BlockBox box) {
        super(e, form, context, box);
    }

    public JComponent create() {
        final JButton button = new JButton();

        String value;
        if (hasAttribute("value")) {
            value = getAttribute("value");
            if (value.length() == 0) {
                value = " ";    //otherwise we get a very short button
            }
        } else {
            value = "Submit";
        }

        applyComponentStyle(button);

        button.setText(value);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                LOGGER.info("Submit pressed: Submit");

                getParentForm().submit(getComponent());
            }
        });

        return button;
    }
    
    public boolean includeInSubmission(final JComponent source) {
        return (source == getComponent());
    }

    protected String[] getFieldValues() {
        return new String[] {
                hasAttribute("value") ? getAttribute("value") : "Submit" // TODO: Don't hardcode 
        };
    }
}
