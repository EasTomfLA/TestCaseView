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
        
        // 新增功能：检测指定的路径是否存在
        const char* paths[] = {
            "/data/data/com.cloudphone.groupcontrol",
            "/data/data/moe.nb4a.debug",
            "/etc/init/init.gamect.rc",
            "/data/data/moe.nb4a",
            "/data/data/com.cloud.androidcontrol",
            "/data/data/com.zidongdianji",
            "/data/data/com.stardust.asstant.inrt",
            "/sdcard/Android/data/com.cloud.androidcontrol",
            "/sdcard/Android/data/com.zidongdianji",
            "/sdcard/Android/data/moe.nb4a",
            "/data/local/tmp/XWCaptureScreen.jar"
        };
        
        std::string access_result = "路径存在检测结果:\n";
        for (const auto& path : paths) {
            int ret = access(path, F_OK);
            if (ret == 0) {
                access_result += std::string(path) + ": 存在\n";
                LOGD("%s 存在", path);
            } else {
                access_result += std::string(path) + ": 不存在 (错误码: " + std::to_string(errno) + 
                                 ", " + std::string(strerror(errno)) + ")\n";
                LOGD("%s 不存在, 错误码: %d, %s", path, errno, strerror(errno));
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