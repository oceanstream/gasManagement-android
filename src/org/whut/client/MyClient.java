package org.whut.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class MyClient{

	private static final int REQUEST_TIMEOUT = 5*1000;
	private static final int SO_TIMEOUT = 10*1000; 

	private static DefaultHttpClient httpClient;
	private static MyClient instance;
	private static String JSESSIONID;

	private DefaultHttpClient getHttpClient(){
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);
		client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8");
		return client;
	}

	private MyClient(){
		MyClient.httpClient = getHttpClient();
	}

	public static MyClient getInstance(){
		if(instance==null){
			synchronized (MyClient.class) {
				if(instance==null){
					instance = new MyClient();
				}
			}
		}
		return instance;
	}

	public boolean login(String username,String password,String sessionGenerateService)throws Exception{
		String ticket = getTicket(username,password);
		if(ticket==null){
			return false;
		}

		JSESSIONID = generateSession(ticket,sessionGenerateService);

		Log.i("msg", "---------->login----->"+JSESSIONID);

		if(JSESSIONID == null||JSESSIONID==""){
			return false;
		}

		return true;
	}

	public String getTicket(String username,String password) throws Exception{
		String ticket = "";
		HttpPost httpPost = new HttpPost("http://59.69.75.186:8080/ICCard/rest/userService/getTicket");
		List <NameValuePair> nvps = new ArrayList <NameValuePair> ();
		nvps.add(new BasicNameValuePair ("username", username));
		nvps.add(new BasicNameValuePair ("password", password));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		synchronized (httpClient) {
			HttpResponse response = httpClient.execute(httpPost);
			String responseBody = getResponseBody(response);
			Log.i("msg", "------>getTicket---->"+responseBody);
			switch (response.getStatusLine().getStatusCode())
			{
			case 200:
				if(responseBody==null){
					return ticket;
				}else{
					ticket = responseBody;
				}
				break;
			}
		}
		return ticket;
	}

	public String generateSession(String ticket,String generateSessionService){

		String sessionId = "";
		HttpGet httpGet = new HttpGet(generateSessionService+"?ticket="+ticket);
		try
		{
			synchronized (httpClient) {
				HttpResponse response = httpClient.execute(httpGet);
				//	EntityUtils.toString(response.getEntity() ,"utf-8");
				switch (response.getStatusLine().getStatusCode())
				{
				case 200:
				{
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (! cookies.isEmpty()){
						for (Cookie cookie : cookies){
							if(cookie.getName().equals("JSESSIONID")){
								sessionId = cookie.getValue();
								Log.i("msg", sessionId);
								break;
							}
						}
					}
					break;
				}
				default:
					break;
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sessionId;
	}


	private String getResponseBody(HttpResponse response){
		StringBuilder sb = new StringBuilder();
		try {
			InputStream instream = response.getEntity().getContent();

			BufferedReader reader =
					new BufferedReader(new InputStreamReader(instream), 65728);
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			instream.close();
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (Exception e) { e.printStackTrace(); }
		return sb.toString();

	}

	synchronized public InputStream DoGetConfigFile(String service){
		HttpGet httpGet = new HttpGet(service);
		
		if(JSESSIONID!=null){
			Log.i("msg", "DoGetConfigFile:"+JSESSIONID);
			httpGet.setHeader("Cookie","JSESSIONID="+JSESSIONID);
		}
		try{
			synchronized (httpClient) {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				switch(httpResponse.getStatusLine().getStatusCode()){
				case 200:
					Log.i("msg", "DoGetConfigFile SUCCESS");
					return httpResponse.getEntity().getContent();
				default:
						break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	//	@SuppressWarnings("rawtypes")
	//	public String doGet(String url,Map params) throws Exception{
	//		
	//        String paramStr = "";  
	//        if (params != null) {  
	//            Iterator iter = params.entrySet().iterator();  
	//            while (iter.hasNext()) {  
	//                Map.Entry entry = (Map.Entry) iter.next();  
	//                Object key = entry.getKey();  
	//                Object val = entry.getValue();  
	//                paramStr += paramStr = "&" + key + "=" + val;  
	//            }  
	//        }  
	//        if (!paramStr.equals("")) {  
	//            paramStr = paramStr.replaceFirst("&", "?");  
	//            url += paramStr;  
	//        }  
	//        HttpGet httpRequest = new HttpGet(url);  
	//        String strResult = "doGetError";  
	//        /* 发送请求并等待响应 */  
	//        HttpResponse httpResponse = httpClient.execute(httpRequest);  
	//        /* 若状态码为200 ok */  
	//        if (httpResponse.getStatusLine().getStatusCode() == 200) {  
	//            /* 读返回数据 */  
	//            strResult = EntityUtils.toString(httpResponse.getEntity());  
	//        } else {  
	//            strResult = "Error Response: " + httpResponse.getStatusLine().toString();  
	//        }  
	//        Log.v("strResult", strResult);  
	//        return strResult; 
	//	}
	//	
	
		public String doPost(String url, List<NameValuePair> params) throws Exception {  
	        /* 建立HTTPPost对象 */  
	        HttpPost httpRequest = new HttpPost(url);  
	        String strResult = "doPostError";  
	        /* 添加请求参数到请求对象 */  
	        if (params != null && params.size() > 0) {  
	            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));  
	        }  
	        if(null != JSESSIONID){  
	            httpRequest.setHeader("Cookie", "JSESSIONID="+JSESSIONID);  
	        }  
	        /* 发送请求并等待响应 */  
	        HttpResponse httpResponse = httpClient.execute(httpRequest);  
	        /* 若状态码为200 ok */  
	        
	        Log.i("msg", httpResponse.getStatusLine().getStatusCode()+"");
	        if (httpResponse.getStatusLine().getStatusCode() == 200) {  
	            /* 读返回数据 */  
	            strResult = EntityUtils.toString(httpResponse.getEntity());  
	            /* 获取cookieStore */  
	            CookieStore cookieStore = httpClient.getCookieStore();  
	            List<Cookie> cookies = cookieStore.getCookies();  
	            for(int i=0;i<cookies.size();i++){  
	                if("JSESSIONID".equals(cookies.get(i).getName())){  
	                    JSESSIONID = cookies.get(i).getValue();  
	                    break;  
	                }  
	            }  
	        }  
	        Log.i("msg", strResult+"----->SessionId="+JSESSIONID);  
	        return strResult;  
	    }  
		
}
