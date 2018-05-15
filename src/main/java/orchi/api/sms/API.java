package orchi.api.sms;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class API extends HttpServlet {
	
	private static Logger log = LoggerFactory.getLogger(API.class);
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		resp.setHeader("Access-Control-Allow-Origin", "*");
		log.info("recibiendo nueva peticion");
		
		PrintWriter out = resp.getWriter();
		JSONObject json = new JSONObject();
		
		//String appKey = req.getParameter("appkey");
		String to = "";
		String toRejects = "";
		String msg = URLEncoder.encode(req.getParameter("msg"), "UTF-8");;
		String username = "root";
		String password = "sms";
		String response = "";
		Boolean send = false;
		int code = 503;
		
		
		String[] testTo = req.getParameterValues("to[]");
		for(String x : testTo){
			//System.out.println("sub: "+Integer.valueOf(x.substring(0, 4))+" ");
			if(x.length() >= 11 && Integer.valueOf(x.substring(0, 4)) != 273){
				to+=" "+x;
			}else{
				toRejects+=","+x;
			}
			
			//System.out.println("numero: "+x);
		}
		to =  to.length() >0 ? URLEncoder.encode(to.substring(1), "UTF-8"):URLEncoder.encode(to, "UTF-8");
		log.info("Mensaje: {}, a: {}",msg,to);
		String get = "http://127.0.0.1:13013/cgi-bin/sendsms?" 
					+ "username=" + username 
					+ "&password=" + password
					+ "&to=" + to 
					+ "&text=" + msg;
		log.info("cuerpo de kannel api {}",get);
		URL url = new URL(get);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		log.info("tratando de conectar con kannel api");
		try {
			
			connection.connect();
			code = connection.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			response  = in.readLine();
			log.info("respuesta (raw) de kannel api {}, {}",code,response);
			in.close();
		} catch (ConnectException e) {
			log.error("error al conectar con kannel api",e.getMessage());
			
			//System.out.println("error ConnectException");
		}catch(IOException e){
			System.out.println("error IOException");
		}

		if (code == 202) {
			send = true;
		} else if (code >= 400 && code < 500) {
			send = false;
		} else if (code >= 503) {
			send = false;
		}	
		
		json.put("api","sms");
		json.put("v","1");
		json.put("send",send);
		json.put("code",code);
		json.put("to",to);
		json.put("rejects",toRejects);
		json.put("msg",msg);
		json.put("resp",response);
		
		log.info("response API SMS {}",json);
		
		out.println(json);
		
		/**out.println("api::sms");
		out.println("v::1");
		out.println("send::"+send);
		out.println("code::"+code);
		out.println("to::"+to);
		out.println("rejets::"+toRejects);
		out.println("msg::"+msg);
		out.print("res::"+response);
		*/
		
		
		
		/*out.println("Api: sms, appKey: " + appKey + "\nUrl: " + get);
		out.println((send ? "El mensaje fue enviado" : "El mensaje no fue enviado" ) +" ,Code: " + code);
		out.println("Respuesta: "+response);
		out.println();
		*/
	}

}
