<template>
  <div class="post-container">
    <h2>发布动态</h2>
    <el-form :model="postForm" label-position="top" :rules="rules" ref="postFormRef">
      <el-form-item label="标题" prop="title">
        <el-input v-model="postForm.title" placeholder="请输入动态标题（选填）"></el-input>
      </el-form-item>
      
      <el-form-item label="内容" prop="content">
        <el-input 
          v-model="postForm.content" 
          type="textarea" 
          :rows="5" 
          placeholder="分享你的健康饮食心得..."
        ></el-input>
      </el-form-item>
      
      <el-form-item label="上传图片">
        <el-upload
          class="upload-demo"
          action="/api/upload"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :on-success="handleSuccess"
          :before-upload="beforeUpload"
          multiple
          :limit="9"
          list-type="picture-card"
          :auto-upload="true"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm(postFormRef)" :loading="loading">发布</el-button>
        <el-button @click="resetForm(postFormRef)">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const postFormRef = ref()
const loading = ref(false)

const postForm = reactive({
  title: '',
  content: '',
  mediaUrls: []
})

const rules = {
  content: [
    { required: true, message: '请输入动态内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '内容长度在1到1000个字符之间', trigger: 'blur' }
  ]
}

const handleRemove = (file, fileList) => {
  const index = postForm.mediaUrls.indexOf(file.response?.data)
  if (index !== -1) {
    postForm.mediaUrls.splice(index, 1)
  }
}

const handlePreview = (file) => {
  // 预览图片
  console.log(file)
}

const handleSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    postForm.mediaUrls.push(response.data)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  
  return true
}

const submitForm = async (formEl) => {
  if (!formEl) return
  
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await axios.post('/api/posts', postForm)
        if (response.data.code === 200) {
          ElMessage.success('动态发布成功')
          router.push('/posts')
        } else {
          ElMessage.error(response.data.message || '发布失败')
        }
      } catch (error) {
        console.error('发布动态出错:', error)
        ElMessage.error('发布失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  postForm.mediaUrls = []
}
</script>

<style scoped>
.post-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  text-align: center;
  color: #409EFF;
}

.el-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>
