#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h> // 获取PID和access函数
#include <fstream>  // 文件操作
#include <sstream>  // 字符串流
#include <sys/stat.h> // 文件权限
#include <errno.h>  // 错误码
#include <cstdio>   // popen, pclose
#include <array>    // std::array
#include <vector>   // std::vector

#define TAG "TestCaseView"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// 保存文件到指定路径
bool saveToFile(const std::string& content, const std::string& path) {
    std::ofstream file(path);
    if (!file.is_open()) {
        LOGE("Failed to create file at %s", path.c_str());
        return false;
    }
    file << content;
    file.close();
    LOGD("File saved to %s", path.c_str());
    return true;
}

// 执行shell命令并返回结果
std::string executeCommand(const char* cmd) {
    std::array<char, 128> buffer;
    std::string result;
    std::unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd, "r"), pclose);
    
    if (!pipe) {
        LOGE("popen() failed for command: %s", cmd);
        return "Error executing command";
    }
    
    while (fgets(buffer.data(), buffer.size(), pipe.get()) != nullptr) {
        result += buffer.data();
    }
    
    return result;
}

// 去除字符串首尾空格的辅助函数
std::string trim(const std::string& str) {
    size_t start = str.find_first_not_of(" \t\r\n");
    if (start == std::string::npos) {
        return "";
    }
    size_t end = str.find_last_not_of(" \t\r\n");
    return str.substr(start, end - start + 1);
}

// 从文件读取路径列表
std::vector<std::string> readPathsFromFile(const std::string& filePath) {
    std::vector<std::string> paths;
    std::ifstream file(filePath);
    
    if (!file.is_open()) {
        LOGE("Failed to open paths configuration file: %s", filePath.c_str());
        return paths;
    }
    
    std::string line;
    while (std::getline(file, line)) {
        // 去除首尾空格（包括空格、制表符、回车符和换行符）
        line = trim(line);
        LOGD("Read path from config line: %s", line.c_str());
        // 忽略空行
        if (!line.empty()) {
            paths.push_back(line);
            LOGD("Read path from config: %s", line.c_str());
        }
    }
    file.close();
    
    LOGD("Loaded %zu additional paths from configuration file", paths.size());
    return paths;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_hello_testcaseview_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    
    std::string result = "Hello from C++";
    
    try {
        // 获取当前进程PID
        pid_t pid = getpid();
        LOGD("Current PID: %d", pid);
        
        // 构造maps文件路径
        std::string maps_path = "/proc/" + std::to_string(pid) + "/maps";
        LOGD("Reading maps from: %s", maps_path.c_str());
        
        // 读取maps文件内容
        std::ifstream maps_file(maps_path);
        if (!maps_file.is_open()) {
            LOGE("Failed to open maps file");
            result += " (Failed to open maps file)";
            return env->NewStringUTF(result.c_str());
        }
        
        std::stringstream buffer;
        buffer << maps_file.rdbuf();
        std::string maps_content = buffer.str();
        maps_file.close();
        
        // 构造输出文件路径 - 同时保存到两个位置
        std::string output_path = "/sdcard/maps_" + std::to_string(pid) + ".txt";
        std::string download_path = "/sdcard/Download/maps_" + std::to_string(pid) + ".txt";
        
        // 保存到主路径
        bool saved = saveToFile(maps_content, output_path);
        bool saved_download = saveToFile(maps_content, download_path);
        
        if (saved) {
            result += " (Maps saved to " + output_path;
            if (saved_download) {
                result += " and " + download_path;
            }
            result += ")";
        } else {
            result += " (Failed to write to SD card. Check permissions)";
            return env->NewStringUTF(result.c_str());
        }
        
        // 设置默认的硬编码路径列表
        std::vector<std::string> paths_to_check = {
            "/vendor/etc/mtk_omx_core.cfg",
            "/vendor/etc/init/ecalcMediaCtl.rc",
            "/vendor/etc/init/rild_ecalc.rc",
            "/vendor/etc/init/hw/init.ecalc.rc",
            "/data/local/tmp/com.cloudecalc.control.apk",
            "/data/local/tmp/T30.tag",
            "/sdcard/Android/data/com.js.tool",
        };
        
        // 检查配置文件是否存在，如果存在，将其中的路径添加到列表中
        const std::string paths_config_file = "/sdcard/exist.cfg";
        if (access(paths_config_file.c_str(), F_OK) == 0) {
            // 如果配置文件存在，从文件中读取路径并添加到列表中
            LOGD("Found configuration file: %s", paths_config_file.c_str());
            std::vector<std::string> additional_paths = readPathsFromFile(paths_config_file);
            
            // 将读取到的路径添加到硬编码路径列表中
            paths_to_check.insert(paths_to_check.end(), additional_paths.begin(), additional_paths.end());
            LOGD("Total paths to check after adding from config: %zu", paths_to_check.size());
        } else {
            LOGD("Configuration file not found, using only hardcoded paths");
        }
        
        // 检测路径是否存在
        std::string access_result = "路径存在检测结果:\n";
        for (const auto& path : paths_to_check) {
            int ret = access(path.c_str(), F_OK);
            if (ret == 0) {
                access_result += path + ": 存在\n";
                LOGD("%s 存在", path.c_str());
            } else {
                access_result += path + ": 不存在 (错误码: " + std::to_string(errno) + 
                                 ", " + std::string(strerror(errno)) + ")\n";
                LOGD("%s 不存在, 错误码: %d, %s", path.c_str(), errno, strerror(errno));
            }
        }
        
        // 将access检测结果写入两个位置
        std::string access_output_path = "/sdcard/access_check.txt";
        std::string access_download_path = "/sdcard/Download/access_check.txt";
        
        bool access_saved = saveToFile(access_result, access_output_path);
        bool access_saved_download = saveToFile(access_result, access_download_path);
        
        if (access_saved) {
            result += " (Access check saved to " + access_output_path;
            if (access_saved_download) {
                result += " and " + access_download_path;
            }
            result += ")";
        } else {
            result += " (Failed to save access check results)";
        }
        
        // 执行 ps -ef 命令并保存结果到两个位置
        LOGD("Executing ps -ef command");
        std::string ps_output = executeCommand("ps -ef");
        
        std::string ps_output_path = "/sdcard/ps_output.txt";
        std::string ps_download_path = "/sdcard/Download/ps_output.txt";
        
        bool ps_saved = saveToFile(ps_output, ps_output_path);
        bool ps_saved_download = saveToFile(ps_output, ps_download_path);
        
        if (ps_saved) {
            result += " (PS output saved to " + ps_output_path;
            if (ps_saved_download) {
                result += " and " + ps_download_path;
            }
            result += ")";
        } else {
            result += " (Failed to save PS command output)";
        }
        
    } catch (const std::exception& e) {
        LOGE("Exception: %s", e.what());
        result += " (Exception: " + std::string(e.what()) + ")";
    } catch (...) {
        LOGE("Unknown exception occurred");
        result += " (Unknown exception occurred)";
    }
    
    return env->NewStringUTF(result.c_str());
}