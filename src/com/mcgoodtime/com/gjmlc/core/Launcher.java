package com.mcgoodtime.com.gjmlc.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by suhao on 2015-6-8-0008.
 */
public class Launcher {

    private static String versionPath = "./.minecraft/versions/";
    private static String versionInfoJson;

    public static void main(String[] args) {
        launch("1.8", "_JAVA7", 2048);
    }

    public static void launch(String version, String username, int maxMemory) {
        String text = loadVersionInfoFile(version);
        System.out.println(text);
        String id = getVersionInfo(text, "id");
        String time = getVersionInfo(text, "time");
        String releaseTime = getVersionInfo(text, "releaseTime");
        String minecraftArguments = getVersionInfo(text, "minecraftArguments");
        int minimumLauncherVersion = getVersionInfoAsInt(text, "minimumLauncherVersion");
        String mainClass = getVersionInfo(text, "mainClass");
        String assets = getVersionInfo(text, "assets");
        String libraries = getLibraries(text);
        System.out.println(libraries);
        tryToLaunch(version, libraries, minecraftArguments, mainClass, assets, username, maxMemory);
    }

    private static void tryToLaunch(String version, String libraries, String minecraftArguments,
                                    String mainClass, String assets, String username, int maxMemory) {
        String nativesPath = versionPath + version + "/" + version + "-" + "Natives";
        String chassPath = versionPath + version + "/" + version + ".jar";
        String arg = minecraftArguments.replace("${auth_player_name}", username)
                .replace("${version_name}", version)
                .replace("${game_directory}", "./.minecraft")
                .replace("${assets_root}", "./.minecraft/assets")
                .replace("${assets_index_name}", assets)
                .replace("${auth_uuid}", "auth_uuid")
                .replace("${auth_access_token}", "auth_access_token")
                .replace("${user_properties}", "{}")
                .replace("${user_type}", "legacy");

        String cmd = "java -Xmx" + maxMemory + "M" + " " + "-Djava.library.path=" + nativesPath + " "
                + "-classpath" + " " + libraries + "\"" + chassPath + "\"" + " " + mainClass + " " + arg;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getVersionInfo(String text, String key) {
        /*
        Get Version Info From Loaded Json
         */
        JSONObject jsonObject = new JSONObject(text);
        String value = null;
        try {
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    private static int getVersionInfoAsInt(String text, String key) {
        /*
        Get Version Info From Loaded Json
         */
        JSONObject jsonObject = new JSONObject(text);
        int value = 0;
        try {
            value = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    private static String loadVersionInfoFile(String version) {
        versionInfoJson = versionPath + version + "/" + version + ".json";
        System.out.println("Version Info Json Path:" + versionInfoJson);

        /*
        Load From Json File
         */
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(versionInfoJson)));
            while( (line = br.readLine())!= null ){
                stringBuffer.append(line);
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    private static String  getLibraries(String text) {
        JSONObject jsonObject = new JSONObject(text);
        JSONArray array = (JSONArray) jsonObject.get("libraries");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < array.length(); i++) {
            JSONObject arrayObject = (JSONObject) array.get(i);
            String lib = arrayObject.get("name").toString();
            String a = lib.substring(0, lib.lastIndexOf(":")).replace(".", "/").replace(":", "/");
            String b = lib.substring(lib.lastIndexOf(":") + 1);
            String c = lib.substring(lib.indexOf(":") + 1).replace(":", "-");
            String libs = "\"" + "./.minecraft/libraries/" + a + "/" + b + "/" + c + ".jar" + "\"" + ";";
            stringBuffer.append(libs);
        }
        return stringBuffer.toString();
    }
}
