project(native-lib)

cmake_minimum_required(VERSION 3.22.1)

#include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)

#add_library(lib_opencv SHARED IMPORTED)
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java4.so)

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/ncnn-20231027-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/opencv-mobile-4.8.1-android/sdk/native/jni)
find_package(OpenCV REQUIRED core imgproc features2d)

add_library(
        ${PROJECT_NAME}
        SHARED
        src/main/cpp/include/NativeSample.cpp
        src/main/cpp/include/NcnnSample.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        ${PROJECT_NAME}
        ${OpenCV_LIBS}
        ncnn
        ${log-lib}
)