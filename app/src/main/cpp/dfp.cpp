#include <jni.h>
#include "fcntl.h"
#include "android/log.h"
#include <unistd.h>
#include <cstdlib>
#include "elf.h"
#include "dfp.h"
#include <string.h>
#include "sys/system_properties.h"
#include <media/NdkMediaDrm.h>
#include "hashmap.h"
#include "utils/allInclude.h"
#include "netlink/ifaddrs.h"
#include "netlink/bionic_netlink.h"

#include <sys/types.h>
#include <dirent.h>

#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>

#include <string>
#include <map>
#include <list>
#include <jni.h>
#include <dlfcn.h>
#include <stddef.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/syscall.h>
#include <cstring>
#include <cstdio>
#include <unistd.h>
#include <stdlib.h>
#include <syscall.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <regex>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <fcntl.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <regex>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include "limits.h"
#include <string.h>
#include <cerrno>
#include <cstring>
#include <climits>
#include "syscall.h"
#include <cstring>
#include <cstdio>
#include <unistd.h>
#include <stdlib.h>
#include <syscall.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <regex>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <fcntl.h>
#include <arpa/inet.h>
#include <netpacket/packet.h>
#include <net/ethernet.h>
#include <jni.h>
//#include <arch.h>
//#include <Log.h>
#include <istream>
#include <sys/types.h>
#include <sys/socket.h>
#include <string.h>
#include <netinet/in.h>
#include <net/ethernet.h>
#include <stdio.h>
#include <stdlib.h>
#include <netpacket/packet.h>
#include <net/ethernet.h>
#include <errno.h>
#include <netinet/in.h>
#include <netinet/ip.h>
#include "netlink/ifaddrs.h"
#include "netlink/bionic_netlink.h"
#include <netdb.h>
#include <unordered_map>


#define  LOG_TAG    "TEST-NATIVE"  // 当前TAG可以自定义
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" void _init(void){LOGD("TEST _init enter");}  // init

void __attribute__((constructor)) myConstructor(void){LOGD("myConstructor enter\n");}// init_array
void __attribute__((constructor)) myConstructor2(void){LOGD("myConstructor2 enter\n");}
void __attribute__((constructor)) myConstructor3(void){LOGD("myConstructor3 enter\n");}

char* jstringTostring(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);

        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_getProp(JNIEnv *env, jclass clazz,jstring inputString) {

    char buf[PROP_VALUE_MAX];
    char* key = jstringTostring(env,inputString);
    __system_property_get(key, buf);

    return env->NewStringUTF(buf);
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_getDrm(JNIEnv *env, jclass clazz) {
    const uint8_t uuid[] = {0xed,0xef,0x8b,0xa9,0x79,0xd6,0x4a,0xce,
                            0xa3,0xc8,0x27,0xdc,0xd5,0x1d,0x21,0xed
    };
    AMediaDrm *mediaDrm = AMediaDrm_createByUUID(uuid);

// 获取 deviceUniqueId
    AMediaDrmByteArray aMediaDrmByteArray;
    AMediaDrm_getPropertyByteArray(mediaDrm,PROPERTY_DEVICE_UNIQUE_ID, &aMediaDrmByteArray);
    jbyteArray ret = env->NewByteArray(aMediaDrmByteArray.length);

    env->SetByteArrayRegion(ret,0,aMediaDrmByteArray.length,(jbyte*)aMediaDrmByteArray.ptr);

    return ret;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_test(JNIEnv *env, jclass clazz) {
    LOGD("%s",getenv("test"));
}

const char * getName(){
    const char* ret = "test";
    return ret;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_getProcessName(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(getName());
}

map_t mymap = hashmap_new();

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_getData(JNIEnv *env, jclass clazz, jstring _key) {
    char * c_key = jstringTostring(env,_key);

    char * value;
    int error = hashmap_get(mymap,c_key,(void**)&value);

    if(value == nullptr){
        return env->NewStringUTF("NNNNULLL");
    }
    return env->NewStringUTF(value);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_setData(JNIEnv *env, jclass clazz, jstring key,
                                                        jstring value) {
    char * c_key = jstringTostring(env,key);
    char * c_value = jstringTostring(env,value);

    int error = hashmap_put(mymap,c_key,c_value);

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_readFile(JNIEnv *env, jclass clazz, jstring path) {
    char * c_path = jstringTostring(env,path);
    FILE * stream = fopen(c_path,"r");
//    int fd = open(c_path,O_RDONLY);
    if(stream==NULL){
        LOGD("打开文件失败");
        return env->NewStringUTF(nullptr);
    }
//    if(fd==-1){
//        LOGD("打开文件失败");
//        return env->NewStringUTF(nullptr);
//    }
    char buf[255];
    fscanf(stream, "%s", buf);
//    read(fd,buf,255);

    return env->NewStringUTF(buf);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_readDir(JNIEnv *env, jclass clazz, jstring path) {
    DIR *pDir = NULL;
    struct dirent * pEnt = NULL;
    unsigned int cnt = 0;

    char * c_path = jstringTostring(env,path);

    if((pDir = opendir(c_path)) == NULL)
    {
        perror("opendir");
        return env->NewStringUTF("ERROR");
    }
    while (1)
    {
        pEnt = readdir(pDir);
        if(pEnt != NULL)
        {
            cnt++;
        }
        else
        {
            break;
        }
    };
    LOGD("总文件数为：%d", cnt);
    return env->NewStringUTF("FINISH");
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_testShell(JNIEnv *env, jclass clazz, jstring cmd) {
    char * c_cmd = jstringTostring(env,cmd);
    FILE* fd = popen(c_cmd,"r");
    if(fd==NULL){
        LOGD("popen失败");
        return env->NewStringUTF(nullptr);
    }
    char buf[255];
    fscanf(fd, "%s", buf);
//    read(fd,buf,255);

    return env->NewStringUTF(buf);
}

typedef std::unordered_map<std::string, std::string> map_tt;


map_tt listmacaddrs() {
    struct ifaddrs *ifap, *ifaptr;
    map_tt macmap;
    if (myGetifaddrs(&ifap) == 0) {
        for (ifaptr = ifap; ifaptr != nullptr; ifaptr = (ifaptr)->ifa_next) {
            if(ifaptr->ifa_addr!= nullptr) {
                sa_family_t family = ((ifaptr)->ifa_addr)->sa_family;
                if (family == AF_PACKET) {
                    //get mac info
                    char macp[INET6_ADDRSTRLEN];
                    auto *sockadd = (struct sockaddr_ll *) (ifaptr->ifa_addr);
                    int i;
                    int len = 0;
                    for (i = 0; i < 6; i++) {
                        len += sprintf(macp + len, "%02X%s", sockadd->sll_addr[i],( i < 5 ? ":" : ""));
                    }
                    macmap.insert(std::make_pair((ifaptr)->ifa_name, macp));
//                    hashmap_put(macmap, (ifaptr)->ifa_name, macp);
                    LOGE("AF_PACKET  %s  %s  ",(ifaptr)->ifa_name,macp);
//                    if(strcmp(ifaptr->ifa_name,"wlan0")== 0){
//                        LOGE("%s  %s  ",(ifaptr)->ifa_name,macp)
//                        freeifaddrs(ifap);
//                        return 1;
//                    }
                }
//                else if(family == AF_INET || family == AF_INET6){
//                    //get v4 & v6 info
//                    char host[NI_MAXHOST];
//                    int ret = getnameinfo(ifaptr->ifa_addr,
//                                          (family == AF_INET) ? sizeof(struct sockaddr_in) :
//                                          sizeof(struct sockaddr_in6),
//                                          host, NI_MAXHOST,
//                                          nullptr, 0, NI_NUMERICHOST);
//                    if (ret != 0) {
//                        LOGE("AF_INET6  getnameinfo() failed   %s  ",gai_strerror(ret));
////                        hashmap_put(macmap, (ifaptr)->ifa_name, "00:00:00:00:00:00");
//                    }
//                    macmap.insert(std::make_pair((ifaptr)->ifa_name, host));
//                    LOGE("AF_INET6 %s %s  ", (ifaptr)->ifa_name, host);
//                }
            }

        }
        freeifaddrs(ifap);
        return macmap;
    } else {
        return macmap;
    }
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_ashenone_dfp_collector_NativeCollector_getMac(JNIEnv *env, jclass clazz) {
    map_tt temp_map = listmacaddrs();
    // 将 C 语言的哈希表转换为 Java Map 对象
    jclass hashMapClass = env->FindClass("java/util/HashMap");
    jmethodID hashMapInit = env->GetMethodID(hashMapClass, "<init>", "()V");
    jobject jMap = env->NewObject(hashMapClass, hashMapInit);

    // 获取 HashMap 的 put 方法 ID
    jmethodID putMethod = env->GetMethodID(hashMapClass, "put",
                                           "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    jclass stringClass = env->FindClass("java/lang/String");

    // 遍历 C++ unordered_map 并填充 Java HashMap
    map_tt::const_iterator it;
    for (it = temp_map.begin(); it != temp_map.end(); ++it) {
        jstring jKey = env->NewStringUTF(it->first.c_str());
//        LOGE("error, ", it -> first.c_str());
        jstring jValue = env->NewStringUTF(it->second.c_str());
        env->CallObjectMethod(jMap, putMethod, jKey, jValue);

        // 清理局部引用
//        env->DeleteLocalRef(stringClass);
//        env->DeleteLocalRef(jKey);
//        env->DeleteLocalRef(jValue);
    }

    // 清理剩余的局部引用
//    env->DeleteLocalRef(hashMapClass);

    return jMap;
}