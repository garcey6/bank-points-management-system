<template>
  <div class="messages">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>留言管理</span>
          <el-tag v-if="pendingCount > 0" type="warning">待回复 {{ pendingCount }}</el-tag>
        </div>
      </template>

      <el-table :data="messages" stripe style="width: 100%">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="联系方式" width="130" />
        <el-table-column prop="content" label="留言内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="留言时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" @click="handleReply(row)">回复</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="replyDialogVisible" title="查看留言并回复" width="600px">
      <div v-if="selectedMessage">
        <el-descriptions :column="2" border style="margin-bottom: 20px">
          <el-descriptions-item label="用户名">{{ selectedMessage.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ selectedMessage.realName }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ selectedMessage.phone }}</el-descriptions-item>
          <el-descriptions-item label="留言时间">{{ selectedMessage.createTime }}</el-descriptions-item>
        </el-descriptions>
        <el-alert
          title="留言内容"
          :description="selectedMessage.content"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        />
        <el-form label-width="80px">
          <el-form-item label="回复内容">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="4"
              placeholder="请输入回复内容">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReply" :loading="replyLoading">
          回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAllMessages, replyMessage, deleteMessage } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminMessages',
  data() {
    return {
      messages: [],
      pendingCount: 0,
      replyDialogVisible: false,
      selectedMessage: null,
      replyContent: '',
      replyLoading: false
    }
  },
  async mounted() {
    await this.loadMessages()
  },
  methods: {
    async loadMessages() {
      try {
        const res = await getAllMessages()
        if (res.code === 200) {
          this.messages = (res.data || []).sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
          this.pendingCount = this.messages.filter(m => m.status === 0).length
        }
      } catch (error) {
        console.error('加载留言失败', error)
      }
    },
    handleReply(message) {
      this.selectedMessage = message
      this.replyContent = message.replyContent || ''
      this.replyDialogVisible = true
    },
    async confirmReply() {
      if (!this.replyContent) {
        ElMessage.warning('请输入回复内容')
        return
      }
      this.replyLoading = true
      try {
        const res = await replyMessage(this.selectedMessage.id, { replyContent: this.replyContent })
        if (res.code === 200) {
          ElMessage.success('回复成功')
          this.replyDialogVisible = false
          await this.loadMessages()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '回复失败')
      } finally {
        this.replyLoading = false
      }
    },
    handleDelete(message) {
      ElMessageBox.confirm('确定要删除这条留言吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteMessage(message.id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadMessages()
          } else {
            ElMessage.error(res.message)
          }
        } catch (error) {
          ElMessage.error(error.response?.data?.message || '删除失败')
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
