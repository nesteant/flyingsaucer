/*
 * {{{ header & license
 * Copyright (c) 2007 Wisconsin Court System
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
package org.xhtmlrenderer.css.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MakeTokens {
    private static final String EOL = System.getProperty("line.separator");
    private static final String INPUT = "C:/eclipseWorkspaceQT/xhtmlrenderer/src/java/org/xhtmlrenderer/css/parser/tokens.txt";
    
    public static final void main(final String[] args) throws IOException {
        final List<String> tokens = new ArrayList<String>();
        
        BufferedReader reader = null; 
        try {
            reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(INPUT)));
            String s;
            while ( (s = reader.readLine()) != null) {
                tokens.add(s);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // ignore
                }
            }
        }
        
        final StringBuffer buf = new StringBuffer();
        
        final int offset = 1;
        for (final String token : tokens) {
            final String id = token.substring(0, token.indexOf(','));
            
            buf.append("\tpublic static final int ");
            buf.append(id);
            buf.append(" = ");
            buf.append(offset);
            buf.append(";");
            buf.append(EOL);
        }
        
        buf.append(EOL);
        
        for (final String token : tokens) {
            final String id = token.substring(0, token.indexOf(','));
            final String descr = token.substring(token.indexOf(',')+1);
            
            buf.append("\tpublic static final Token TK_");
            buf.append(id);
            buf.append(" = new Token(");
            buf.append(id);
            buf.append(", \"");
            buf.append(id);
            buf.append("\", \"");
            buf.append(descr);
            buf.append("\");");
            buf.append(EOL);
        }
        
        buf.append(EOL);
        
        System.out.println(buf.toString());
    }
}
