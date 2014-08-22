LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := computeposition
LOCAL_SRC_FILES := computeposition.c
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog  -mhard-float
include $(BUILD_SHARED_LIBRARY)
