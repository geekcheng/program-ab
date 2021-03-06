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
    * implements AIML Map
    *
    * A map is a function from one string set to another.
    * Elements of the domain are called keys and elements of the range are called values.
    *
*/
public class AIMLMap extends HashMap<String, String> {
    public String  mapName;
    String host; // for external maps
    String botid; // for external maps
    boolean isExternal = false;

       /**
        * constructor to create a new AIML Map
        *
        * @param name      the name of the map
        */
    public AIMLMap (String name) {
        super();
        this.mapName = name;
    }

       /**
        * return a map value given a key
        *
        * @param key          the domain element
        * @return             the range element or a string indicating the key was not found
        */
    public String get(String key) {
        String value;
        if (mapName.equals(MagicStrings.map_successor)) {
            try {
                int number = Integer.parseInt(key);
                return String.valueOf(number+1);
            } catch (Exception ex) {
                return MagicStrings.unknown_map_value;
            }
        }
        else if (mapName.equals(MagicStrings.map_predecessor)) {
            try {
                int number = Integer.parseInt(key);
                return String.valueOf(number-1);
            } catch (Exception ex) {
                return MagicStrings.unknown_map_value;
            }
        }        
        else value = super.get(key);
        if (value == null) value = MagicStrings.unknown_map_value;
        ResourceProvider.Log.info("AIMLMap get "+key+"="+value);
        return value;
    }

       /**
        * put a new key, value pair into the map.
        *
        * @param key    the domain element
        * @param value  the range element
        * @return       the value
        */
    public String put(String key, String value) {
        //ResourceProvider.Log.info("AIMLMap put "+key+"="+value);
        return super.put(key, value);
    }

       /**
        * reads an input stream and loads a map into the bot.
        *
        * @param in    input stream
        * @param bot   AIML bot
        * @return      number of map elements loaded
        */
    public int readAIMLMapFromInputStream(InputStream in, Bot bot)  {
        int cnt=0;
        String strLine;
        //Read File Line By Line
        try {
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));            
            while ((strLine = br.readLine()) != null  && strLine.length() > 0)   {
                String[] splitLine = strLine.split(":");
                //ResourceProvider.Log.info("AIMLMap line="+strLine);
                if (splitLine.length >= 2) {
                    cnt++;
                    if (strLine.startsWith(MagicStrings.remote_map_key)) {
                        if (splitLine.length >= 3) {
                            host = splitLine[1];
                            botid = splitLine[2];
                            isExternal = true;
                            ResourceProvider.Log.info("Created external map at "+host+" "+botid);
                        }
                    }
                    else {
                      String key = splitLine[0].toUpperCase();
                      String value = splitLine[1];
                      // assume domain element is already normalized for speedier load
                      //key = bot.preProcessor.normalize(key).trim();
                      put(key, value);
                    }
                }
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	
        }
        return cnt;
    }

       /**
        * read an AIML map for a bot
        *
        * @param bot          the bot associated with this map.
        */
    public void readAIMLMap (Bot bot) {
        ResourceProvider.Log.info("Reading AIML Map "+MagicStrings.maps_path+"/"+mapName+".txt");
        try{
            // Open the file that is the first
            // command line parameter
            IOResource file = ResourceProvider.IO.getResource(MagicStrings.maps_path+"/"+mapName+".txt");
            if (file.exists()) {
                readAIMLMapFromInputStream(file.input(), bot);                
            }
            else ResourceProvider.Log.info(MagicStrings.maps_path+"/"+mapName+".txt not found");
        }catch (Exception e){//Catch exception if any
           ResourceProvider.Log.error("Error: " + e.getMessage());
        }

    }

}
