package za.co.exampleapp.masilibalestoto.exampleapp.helper;

//This class is for storing all URLs as a model of URLs

public class Config_URL
{
	private static String base_URL = "http://192.168.32.186:8080/";	 // Wayne if you see this comment, change this to point to your host server IP
	// Server user login url
	public static String URL_LOGIN = base_URL+"web_api/";
	// Server user register url
	public static String URL_REGISTER = base_URL+"web_api/";
	// Server values upload url
	public static String URL_UPLOAD_VALUES = base_URL+"web_api/";
}
