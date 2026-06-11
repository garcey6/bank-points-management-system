<template>
  <div class="profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      <div class="avatar-section">
        <el-avatar :size="120" :src="userForm.avatar">
          {{ userForm.realName?.charAt(0) || 'U' }}
        </el-avatar>
        <el-button type="primary" size="small" @click="avatarUploadDialogVisible = true">上传头像</el-button>
      </div>
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="银行卡号">
          <el-input v-model="userForm.bankCard"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="updateLoading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="avatarUploadDialogVisible" title="上传头像" width="500px">
      <div class="avatar-upload-content">
        <div class="preview-section">
          <h3>预览效果</h3>
          <div class="preview-container">
            <el-avatar :size="150" :src="previewImage">
              {{ userForm.realName?.charAt(0) || 'U' }}
            </el-avatar>
          </div>
        </div>
        <div class="upload-section">
          <h3>选择图片</h3>
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-change="handleAvatarChange"
            accept="image/*"
            :auto-upload="false">
            <el-button type="primary">选择图片</el-button>
          </el-upload>
          <div v-if="previewImage" class="image-link-section">
            <h4>图片链接</h4>
            <el-input v-model="previewImage" readonly />
          </div>
          <div class="upload-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>请选择JPG、PNG格式的图片，大小不超过2MB</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="avatarUploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="uploadAvatar" :disabled="!previewImage">确定上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { updateUser, getUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

export default {
  name: 'UserProfile',
  components: {
    InfoFilled
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      userForm: {
        id: null,
        username: '',
        realName: '',
        phone: '',
        email: '',
        bankCard: '',
        avatar: ''
      },
      rules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      updateLoading: false,
      avatarUploadDialogVisible: false,
      avatarFile: null,
      previewImage: ''
    }
  },
  async mounted() {
    // 如果localStorage中没有用户信息，或者用户信息中没有id，使用默认的用户ID
    const userId = this.user.id || 1
    console.log('获取用户信息，用户ID:', userId)
    try {
      const res = await getUserInfo(userId)
      console.log('获取用户信息成功:', res)
      if (res.code === 200) {
        this.user = res.data
        this.userForm = { ...this.user }
        // 存储到localStorage中
        localStorage.setItem('user', JSON.stringify(res.data))
        console.log('用户信息已存储到localStorage')
      } else {
        console.error('获取用户信息失败:', res.message)
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  },
  methods: {
    async handleUpdate() {
      console.log('开始更新用户信息:', this.userForm)
      this.$refs.userFormRef.validate(async (valid) => {
        if (valid) {
          this.updateLoading = true
          try {
            // 确保包含所有必填字段
            const updateData = {
              ...this.user, // 使用原始用户数据作为基础
              ...this.userForm, // 覆盖表单中的修改
            }
            console.log('发送的更新数据:', updateData)
            const res = await updateUser(updateData)
            console.log('更新用户信息成功:', res)
            if (res.code === 200) {
              ElMessage.success('更新成功')
              localStorage.setItem('user', JSON.stringify(res.data))
              this.user = res.data
              this.userForm = { ...res.data }
              console.log('更新后的用户信息已存储到localStorage')
            } else {
              ElMessage.error(res.message)
              console.error('更新用户信息失败:', res.message)
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '更新失败')
            console.error('更新用户信息失败', error)
          } finally {
            this.updateLoading = false
          }
        }
      })
    },
    handleAvatarChange(file) {
      this.avatarFile = file.raw
      const reader = new FileReader()
      reader.onload = (e) => {
        this.previewImage = e.target.result
      }
      reader.readAsDataURL(this.avatarFile)
    },
    async uploadAvatar() {
      if (!this.avatarFile) {
        ElMessage.warning('请选择图片')
        return
      }
      // 这里模拟上传，实际项目中应该调用后端API
      this.userForm.avatar = this.previewImage
      ElMessage.success('头像上传成功')
      this.avatarUploadDialogVisible = false
      this.previewImage = ''
      // 自动保存修改
      await this.handleUpdate()
    }
  }
}
</script>

<style scoped>
.profile {
  flex: 1;
  padding: 20px;
  min-height: calc(100vh - 40px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  gap: 15px;
}

.avatar-section .el-avatar {
  border: 4px solid #f0f2f5;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.avatar-upload-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding: 20px 0;
}

.preview-section {
  text-align: center;
}

.preview-section h3,
.upload-section h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px dashed #d9d9d9;
}

.preview-container .el-avatar {
  border: 4px solid #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  color: #409eff;
  font-size: 14px;
}

.upload-tip el-icon {
  font-size: 16px;
}

.image-link-section {
  margin-top: 15px;
}

.image-link-section h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.image-link-section .el-input {
  width: 100%;
}
</style>
