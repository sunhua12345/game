package sun.game.def.xe;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.game.def.entity.Describle;


public class DefDataTool
{
    private static Logger logger = LoggerFactory.getLogger(DefDataTool.class);

    private static Map<String, List<Object>> baseDefsMap;

    private static DefDataTool defDataTool;

    private DefDataTool() throws Exception
    {
        // 加载配置类
        ClassManager.instance();
        load();
    }

    public static DefDataTool instance()
    {
        if (defDataTool == null)
        {
            try
            {
                defDataTool = new DefDataTool();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return defDataTool;
    }

    // 重载数据
    public void load() throws Exception
    {
        baseDefsMap = new HashMap<String, List<Object>>();
        // 获得server路径下的所有子文件
        Set<File> files = getFilesAll("cs.available");
        for (File file : files)
        {
            // 将配置信息加载到内存
            List<Object> baseDefs = loadXml(file, "cs.available");
            for (Object baseDef : baseDefs)
            {
                String clazzName = baseDef.getClass().getName();
                if (baseDefsMap.containsKey(clazzName))
                {
                    baseDefsMap.get(clazzName).add(baseDef);
                }
                else
                {
                    List<Object> defs = new ArrayList<Object>();
                    defs.add(baseDef);
                    baseDefsMap.put(clazzName, defs);
                }
            }
        }
    }

    /**
     * 加载文件
     * 
     * @param xmlRealPath
     *            真实xml路径
     */
    public static List<Object> loadXml(File file, String right)
            throws Exception
    {

        InputStream inputStream = null;

        try
        {
            if (!file.exists())
            {
                logger.error("file is not exsit");
                throw new Exception("file is not exsit!!!");
            }
            logger.info("load {}", file.getName());
            inputStream = new FileInputStream(file);

            return DataSettingTool.parseByInputStream(inputStream);
        }
        catch (Exception e)
        {
            throw new RuntimeException("parse xml" + file + " error!", e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (Exception e2)
            {
                throw e2;
            }
        }
    }

    /**
     * 获得路径下的所有子文件
     * 
     * @return Set<File> 返回类型
     * @throws
     */
    public Set<File> getFilesAll(String prop)
    {
        // 获得系统配置
        Properties properties = ConfigTool.instance().getSystemProperties();
        // 获得xml路径
        String xmlPath = properties.getProperty("cs.files");
        // 获得web中xml的服务端配置路径
        String webPath = properties.getProperty(prop);

        // 获得路径下的所有子文件
        Set<File> files = getDefXmlFile(webPath, xmlPath);
        return files;
    }

    /**
     * 返回配置文件
     * 
     * @param webPath
     *            web中xml路径
     * @param xmlPath
     *            xml文件信息
     * @return Set<File> 返回所有配置文件
     * @throws
     */
    public Set<File> getDefXmlFile(String webPath, String xmlPath)
    {

        File webfile = new File(System.getProperty("user.dir") + webPath);
        Set<File> files = new HashSet<File>();
        getAllChildFile(webfile, files);

        Set<File> result = new HashSet<File>();
        for (File file : files)
        {
            String fileName = file.getName();
            if (fileName.matches(RegixTool.toRegixStr(xmlPath)))
            {
                result.add(file);
            }
        }

        return result;
    }

    /**
     * 返回所有子文件
     * 
     * @param file
     *            父类文件
     * @param files
     *            配置文件集合
     * @throws
     */
    public void getAllChildFile(File file, Set<File> files)
    {
        if (!file.exists())
        {
            logger.error("{} is not exsit", files);
        }

        if (file.isFile())
        {
            files.add(file);
        }
        else
        {
            File[] filechilds = file.listFiles();
            if (filechilds == null || filechilds.length == 0)
            {
                // logger.debug("config file has none");
                return;
            }
            for (File filechild : filechilds)
            {
                getAllChildFile(filechild, files);
            }
        }
    }

    public Map<String, List<Object>> getBaseObjectMap() throws Exception
    {
        if (baseDefsMap == null || baseDefsMap.size() == 0)
        {
            load();
        }
        return baseDefsMap;
    }

    /**
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     *             获得类描述信息
     * 
     * @param clazz
     *            传入的类
     * @return String[] 返回类名与类描述
     * @throws
     */
    public static String getClassDescrible(Class<?> clazz)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException
    {
        if (clazz == null)
        {
            System.out.println("类为空");
            return null;
        }
        boolean hasDescrible = false;
        String value = "";
        if(clazz.isAnnotationPresent(Describle.class)){
			hasDescrible = true;
			Annotation annotation = clazz.getAnnotation(Describle.class);
			value = (String) annotation.annotationType().getDeclaredMethod("value").invoke(annotation);
		}
        if (!hasDescrible)
        {
            logger.error("{} has no @Describle annotation", clazz.getName());
            return null;
        }

        return value;
    }
}
