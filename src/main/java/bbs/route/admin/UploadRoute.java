package bbs.route.admin;

import java.io.File;
import java.util.Date;

import bbs.Constant;
import bbs.route.RouteBase;
import blade.Blade;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.IOKit;
import blade.kit.StringKit;
import blade.kit.json.JSONObject;
import blade.route.HttpMethod;
import blade.servlet.FileItem;
import blade.servlet.Request;
import blade.servlet.Response;
import blade.servlet.ServletFileUpload;

@Path("/admin")
public class UploadRoute implements RouteBase {
	
	/**
	 * 上传文件
	 */
	@Route(value="files/upload", method = HttpMethod.POST)
	public void upload(Request request, Response response){
		
		ServletFileUpload fileUpload = ServletFileUpload.parseRequest(request.servletRequest());
		
		boolean isMultipart = fileUpload.isMultipartContent(request.servletRequest());
		
		if(isMultipart){
			
			FileItem fileItem = fileUpload.fileItem("file");
			
			String suffix = FileKit.getExtension(fileItem.getFileName());
			if(StringKit.isNotBlank(suffix)){
				suffix = "." + suffix;
			}
			
			String saveName = DateKit.dateFormat(new Date(), "yyyyMMddHHmmssSSS")  + "_" + StringKit.getRandomChar(10) + suffix;
			
			File file = new File(Blade.webRoot() + File.separator + Constant.UPLOAD_FOLDER + File.separator + saveName);
			
			IOKit.write(fileItem.getFileContent(), file);
			
			JSONObject jsonObject = new JSONObject();
			
			if(file.exists()){
				
				String prfix = request.url().replaceFirst(request.servletPath(), "/");
				String filePath = Constant.UPLOAD_FOLDER + "/" + saveName;
				String url = prfix + filePath;
				
				jsonObject.put("success", 1);
				jsonObject.put("message", "上传成功");
				jsonObject.put("filename", fileItem.getFileName());
				jsonObject.put("filepath", filePath);
				jsonObject.put("url", url);
			} else {
				jsonObject.put("success", 0);
				jsonObject.put("message", "上传失败");
			}
			
			response.json(jsonObject.toString());
		}
		
		
	}
	
	/**
	 * 上传图片
	 */
	@Route(value="uploadimg", method = HttpMethod.POST)
	public void uploadImg(Request request, Response response){
		
		ServletFileUpload fileUpload = ServletFileUpload.parseRequest(request.servletRequest());
		
		boolean isMultipart = fileUpload.isMultipartContent(request.servletRequest());
		
		if(isMultipart){
			
			FileItem fileItem = fileUpload.fileItem("image");
			
			String suffix = FileKit.getExtension(fileItem.getFileName());
			if(StringKit.isNotBlank(suffix)){
				suffix = "." + suffix;
			}
			
			String saveName = DateKit.dateFormat(new Date(), "yyyyMMddHHmmssSSS")  + "_" + StringKit.getRandomChar(10) + suffix;
			
			File file = new File(Blade.webRoot() + File.separator + Constant.UPLOAD_FOLDER + File.separator + saveName);
			
			IOKit.write(fileItem.getFileContent(), file);
			
			JSONObject jsonObject = new JSONObject();
			
			if(file.exists()){
				
				String prfix = request.url().replaceFirst(request.servletPath(), "/");
				String filePath = Constant.UPLOAD_FOLDER + "/" + saveName;
				String url = prfix + filePath;
				
				jsonObject.put("id", saveName);
				jsonObject.put("url", url);
				jsonObject.put("success", 1);
				jsonObject.put("message", "上传成功");
				
			} else {
				jsonObject.put("success", 0);
				jsonObject.put("message", "上传失败");
			}
			
			response.json(jsonObject.toString());
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
