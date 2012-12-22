LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := minizip
LOCAL_SRC_FILES := ioapi_buf.c ioapi_mem.c ioapi.c unzip.c zip.c
LOCAL_SRC_FILES += minizip.cpp
LOCAL_LDLIBS += -lz

include $(BUILD_SHARED_LIBRARY)