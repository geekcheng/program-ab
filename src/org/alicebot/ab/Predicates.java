package org.alicebot.ab;
/* Program AB Reference AIML 2.0 implementation
        Copyright (C) 2013 ALICE A.I. Foundation
        Contact: info@alicebot.org

        This library is free software; you can redistribute it and/or
        modify it under the terms of the GNU Library General Public
        License as published by the Free Software Foundation; either
        version 2 of the License, or (at your option) any later version.

        This library is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
        Library General Public License for more details.

        You should have received a copy of the GNU Library General Public
        License along with this library; if not, write to the
        Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
        Boston, MA  02110-1301, USA.
*/
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.miguelff.alicebot.ab.ResourceProvider;
import org.miguelff.alicebot.ab.io.IOResource;

/**
 * Manage client predicates
 *
 */
public class Predicates extends HashMap<String, String> {
    /**
     * save a predicate value
     *
     * @param key         predicate name
     * @param value       predicate value
     * @return            predicate value
     */
    public String put(String key, String value) {
        if (MagicBooleans.trace_mode) ResourceProvider.Log.info("Setting predicate "+key+" to "+value);
        return super.put(key, value);
    }

    /**
     * get a predicate value
     *
     * @param key predicate name
     * @return    predicate value
     */
    public String get(String key) {
        String result = super.get(key);
        if (result == null) return MagicStrings.unknown_predicate_value;
        else return result;
    }

    /**
     * Read predicate default values from an input stream
     *
     * @param in input stream
     */
    public void getPredicateDefaultsFromInputStream (InputStream in)  {
        String strLine;
        try {
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if (strLine.contains(":")) {
                    String property = strLine.substring(0, strLine.indexOf(":"));
                    String value = strLine.substring(strLine.indexOf(":")+1);
                    put(property, value);
                }
            }
            
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** read predicate defaults from a file
     *
     * @param filename        name of file
     */
    void getPredicateDefaults (String filename) {
        try{
            // Open the file that is the first
            // command line parameter
            IOResource file = ResourceProvider.IO.getResource(filename);
            if (file.exists()) {
                // Get the object
                getPredicateDefaultsFromInputStream(file.input());
            }
        }catch (Exception e){//Catch exception if any
           ResourceProvider.Log.error(e.getMessage());
        }
    }
}


