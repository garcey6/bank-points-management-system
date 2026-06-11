<template>
  <div class="messages">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">留言反馈</span>
          <el-button type="primary" @click="showAddDialog">我要留言</el-button>
        </div>
      </template>

      <div v-if="messages.length > 0" class="message-list">
        <div v-for="message in messages" :key="message.id" class="message-item">
          <div class="message-header">
            <el-tag :type="message.status === 1 ? 'success' : 'warning'" size="small">
              {{ message.status === 1 ? '已回复' : '待回复' }}
            </el-tag>
            <span class="message-time">{{ message.createTime }}</span>
          </div>
          <div class="message-content">
            <p>{{ message.content }}</p>
          </div>
          <div v-if="message.replyContent" class="reply-box">
            <div class="reply-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>管理员回复</span>
              <span class="reply-time">{{ message.replyTime }}</span>
            </div>
            <p>{{ message.replyContent }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无留言" />
    </el-card>

    <el-dialog v-model="addDialogVisible" title="写留言" width="500px">
      <el-form :model="messageForm" label-width="80px">
        <el-form-item label="留言内容">
          <el-input
            v-model="messageForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入您的留言内容...">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMessage" :loading="submitLoading">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getUserMessages, addMessage } from '@/api/user'
import { ElMessage } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'

export default {
  name: 'UserMessages',
  components: {
    ChatDotRound
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      messages: [],
      addDialogVisible: false,
      messageForm: {
        content: ''
      },
      submitLoading: false
    }
  },
  async mounted() {
    await this.loadMessages()
  },
  methods: {
    async loadMessages() {
      try {
        const res = await getUserMessages(this.user.id)
        if (res.code === 200) {
          this.messages = (res.data || []).sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        }
      } catch (error) {
        console.error('加载留言失败', error)
      }
    },
    showAddDialog() {
      this.messageForm.content = ''
      this.addDialogVisible = true
    },
    async submitMessage() {
      if (!this.messageForm.content) {
        ElMessage.warning('请输入留言内容')
        return
      }
      this.submitLoading = true
      try {
        const res = await addMessage({
          userId: this.user.id,
          content: this.messageForm.content
        })
        if (res.code === 200) {
          ElMessage.success('留言成功')
          this.addDialogVisible = false
          await this.loadMessages()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '提交失败')
      } finally {
        this.submitLoading = false
      }
    }
  }
}
</script>

<style scoped>
.messages {
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

.title {
  font-size: 18px;
  font-weight: bold;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.message-item:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
}

.reply-box {
  margin-top: 15px;
  padding: 15px;
  background: #f0f9eb;
  border-radius: 8px;
  border-left: 3px solid #67c23a;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #67c23a;
  font-weight: bold;
  margin-bottom: 8px;
}

.reply-time {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
  margin-left: auto;
}

.reply-box p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}
</style>
