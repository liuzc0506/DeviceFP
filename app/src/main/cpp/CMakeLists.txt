
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.6.4111459)
set(CMAKE_CXX_STANDARD 14)
#添加内联支持
enable_language(C ASM)

# Declares and names the project.

project("dfp")

#开始优化
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O")
# inline
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility-inlines-hidden")


include_directories(
        ./
        ./utils/
        ./netlink/
        ./Substrate/
)

if (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "arm64-v8a")
    set(syscall netlink/syscall64.s)
elseif (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "armeabi-v7a")
    set(syscall netlink/syscall32.s)
endif ()

find_library(
        log-lib
        log
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
            dfp

             # Sets the library as a shared library.
            SHARED

             # Provides a relative path to your source file(s).
            dfp.cpp
            hashmap.cpp
            netlink/bionic_netlink.cpp
            netlink/ifaddrs.cpp
            ${syscall}
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       dfp
                        mediandk.so

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )