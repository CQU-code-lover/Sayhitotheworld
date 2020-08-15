package tool;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicTool {
    public static void setCharacterEncoding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
    }
    public static void clearSession(HttpSession httpSession){
        httpSession.removeAttribute("user");
        httpSession.removeAttribute("admin");
    }
    public static String generateRandomEmailCode(){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int m = random.nextInt(1000000);
        while(m<100000){
            m*=10;
        }
        return ""+m;
    }

    public static String generateRedeemCode(){
        return "ABC123";
    }

    public static int str2int(String input){
        if(input==null){
            return -1;
        }
        try{
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }

    public static short str2short(String input){
        if(input==null){
            return -1;
        }
        try{
            return Short.parseShort(input);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }

    public static String getStateStr(int state){
        return "{state:"+state+"}";
    }

    public static boolean handlePicUpload(HttpServletRequest request){
        // 上传文件存储目录
        String UPLOAD_DIRECTORY = "uploadImage";

        // 上传配置
        int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
        int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
        int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
        // 检测是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            return false;
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // 中文处理
        upload.setHeaderEncoding("UTF-8");

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;


        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // 解析请求的内容提取文件数据
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String pattern_str = "\\.([a-z]|[A-Z])+";
                        Pattern pattern = Pattern.compile(pattern_str);
                        Matcher matcher = pattern.matcher(fileName);
                        String tail = null;
                        if(matcher.find()){
                            tail = matcher.group(0);
                            String filePath = uploadPath + File.separator + System.currentTimeMillis()+tail;
                            File storeFile = new File(filePath);
                            // 保存文件到硬盘
                            item.write(storeFile);
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}