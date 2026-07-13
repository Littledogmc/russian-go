<script setup lang="ts">
/*
 * Admin dashboard page.
 * Manage users (change roles) and wordbooks (edit name/count).
 */
import { ref, onMounted } from 'vue'
import { getUsers, updateUserRole, getAdminWordbooks, updateWordbook } from '@/api/admin'
import type { AdminUser, AdminWordbook } from '@/api/admin'

const lsUsers = ref<AdminUser[]>([])
const lsWordbooks = ref<AdminWordbook[]>([])
const isLoading = ref(true)
const isError = ref(false)
const errorMsg = ref('')

const editingUserId = ref<number | null>(null)
const editingRole = ref<number>(1)
const editingWbId = ref<number | null>(null)
const editingWbName = ref('')
const editingWbCount = ref(0)

onMounted(async () => {
  try {
    const [users, wbs] = await Promise.all([getUsers(), getAdminWordbooks()])
    lsUsers.value = users
    lsWordbooks.value = wbs
  } catch {
    isError.value = true
    errorMsg.value = 'Failed to load admin data. Make sure you are logged in as admin.'
  } finally {
    isLoading.value = false
  }
})

async function saveUserRole(userId: number) {
  try {
    await updateUserRole(userId, editingRole.value)
    const u = lsUsers.value.find((x) => x.id === userId)
    if (u) u.role = editingRole.value
    editingUserId.value = null
  } catch {
    errorMsg.value = 'Failed to update user role'
  }
}

async function saveWordbook(wbId: number) {
  try {
    await updateWordbook(wbId, editingWbName.value, editingWbCount.value)
    const w = lsWordbooks.value.find((x) => x.id === wbId)
    if (w) {
      w.name = editingWbName.value
      w.wordCount = editingWbCount.value
    }
    editingWbId.value = null
  } catch {
    errorMsg.value = 'Failed to update wordbook'
  }
}

function startEditUser(u: AdminUser) {
  editingUserId.value = u.id
  editingRole.value = u.role
}

function startEditWb(w: AdminWordbook) {
  editingWbId.value = w.id
  editingWbName.value = w.name
  editingWbCount.value = w.wordCount
}

function roleLabel(r: number): string {
  if (r === 0) return '管理员'
  if (r === 1) return '普通用户'
  if (r === -1) return '已封禁'
  return '未知'
}

function roleClass(r: number): string {
  if (r === 0) return 'oj-badge--info'
  if (r === 1) return 'oj-badge--success'
  if (r === -1) return 'oj-badge--danger'
  return ''
}
</script>

<template>
  <div class="dashboard-page">
    <div class="oj-page-header">
      <h1>⚙️ 管理后台</h1>
      <p class="text-secondary">管理用户和词书</p>
    </div>

    <div v-if="isError" class="oj-card mb-6" style="border-color: var(--oj-danger)">
      <div class="oj-card__body">
        <p style="color: var(--oj-danger)">{{ errorMsg }}</p>
      </div>
    </div>

    <!-- User management -->
    <div class="oj-card mb-6">
      <div class="oj-card__header">
        <h3>👥 用户管理（{{ lsUsers.length }} 人）</h3>
      </div>
      <div class="oj-card__body" style="padding: 0">
        <table class="oj-table" v-if="!isLoading && lsUsers.length > 0">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>注册时间</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in lsUsers" :key="u.id">
              <td class="text-muted">{{ u.id }}</td>
              <td style="font-weight: 500">{{ u.username }}</td>
              <td class="text-secondary" style="font-size: 13px">{{ u.email || '-' }}</td>
              <td>
                <template v-if="editingUserId === u.id">
                  <select
                    v-model.number="editingRole"
                    class="oj-input"
                    style="width: 120px; padding: 4px 8px; font-size: 13px"
                  >
                    <option :value="0">管理员</option>
                    <option :value="1">普通用户</option>
                    <option :value="-1">封禁</option>
                  </select>
                </template>
                <span v-else class="oj-badge" :class="roleClass(u.role)">{{
                  roleLabel(u.role)
                }}</span>
              </td>
              <td class="text-muted" style="font-size: 13px">{{ u.createdAt }}</td>
              <td style="text-align: right">
                <template v-if="editingUserId === u.id">
                  <button class="oj-btn oj-btn--sm oj-btn--success" @click="saveUserRole(u.id)">
                    保存
                  </button>
                  <button
                    class="oj-btn oj-btn--sm oj-btn--outline"
                    @click="editingUserId = null"
                    style="margin-left: 4px"
                  >
                    取消
                  </button>
                </template>
                <button v-else class="oj-btn oj-btn--sm oj-btn--outline" @click="startEditUser(u)">
                  编辑
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!isLoading" class="empty-state"><p>暂无用户数据</p></div>
        <div v-else class="empty-state"><p>加载中...</p></div>
      </div>
    </div>

    <!-- Wordbook management -->
    <div class="oj-card">
      <div class="oj-card__header">
        <h3>📚 词书管理（{{ lsWordbooks.length }} 本）</h3>
      </div>
      <div class="oj-card__body" style="padding: 0">
        <table class="oj-table" v-if="!isLoading && lsWordbooks.length > 0">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>词数</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="w in lsWordbooks" :key="w.id">
              <td class="text-muted">{{ w.id }}</td>
              <td>
                <input
                  v-if="editingWbId === w.id"
                  v-model="editingWbName"
                  class="oj-input"
                  style="width: 200px; padding: 4px 8px; font-size: 13px"
                />
                <span v-else style="font-weight: 500">{{ w.name }}</span>
              </td>
              <td>
                <input
                  v-if="editingWbId === w.id"
                  v-model.number="editingWbCount"
                  type="number"
                  class="oj-input"
                  style="width: 80px; padding: 4px 8px; font-size: 13px"
                />
                <span v-else class="text-secondary">{{ w.wordCount }} 词</span>
              </td>
              <td style="text-align: right">
                <template v-if="editingWbId === w.id">
                  <button class="oj-btn oj-btn--sm oj-btn--success" @click="saveWordbook(w.id)">
                    保存
                  </button>
                  <button
                    class="oj-btn oj-btn--sm oj-btn--outline"
                    @click="editingWbId = null"
                    style="margin-left: 4px"
                  >
                    取消
                  </button>
                </template>
                <button v-else class="oj-btn oj-btn--sm oj-btn--outline" @click="startEditWb(w)">
                  编辑
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!isLoading" class="empty-state"><p>暂无词书数据</p></div>
        <div v-else class="empty-state"><p>加载中...</p></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  max-width: 1000px;
  margin: 0 auto;
}
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--oj-text-muted);
}
</style>
