<template>
  <div class="file-comparator">
    <el-row :gutter="20">
      <!-- 左侧：配置和文件上传 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>配置选择</span>
              <el-button type="primary" @click="showConfigDialog = true">新建配置</el-button>
            </div>
          </template>
          
          <el-select v-model="selectedConfig" placeholder="选择配置" style="width: 100%">
            <el-option
              v-for="config in configs"
              :key="config"
              :label="config"
              :value="config"
            />
          </el-select>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>文件上传</span>
          </template>
          
          <div class="upload-section">
            <h4>文件 1</h4>
            <el-upload
              drag
              action="/api/upload"
              :auto-upload="false"
              :on-change="handleFile1Change"
              :limit="1"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽文件到此处或 <em>点击上传</em>
              </div>
            </el-upload>
          </div>

          <div class="upload-section" style="margin-top: 20px">
            <h4>文件 2</h4>
            <el-upload
              drag
              action="/api/upload"
              :auto-upload="false"
              :on-change="handleFile2Change"
              :limit="1"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽文件到此处或 <em>点击上传</em>
              </div>
            </el-upload>
          </div>

          <el-button 
            type="primary" 
            style="margin-top: 20px; width: 100%"
            :disabled="!canCompare"
            @click="startCompare"
          >
            开始对比
          </el-button>
        </el-card>
      </el-col>

      <!-- 右侧：对比结果 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>对比结果</span>
          </template>
          
          <div v-if="loading" class="loading">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="result" class="result">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="仅在文件1">{{ result.onlyInFile1?.length || 0 }} 条</el-descriptions-item>
              <el-descriptions-item label="仅在文件2">{{ result.onlyInFile2?.length || 0 }} 条</el-descriptions-item>
              <el-descriptions-item label="已修改">{{ result.modified?.length || 0 }} 条</el-descriptions-item>
              <el-descriptions-item label="相同">{{ result.identicalCount || 0 }} 条</el-descriptions-item>
            </el-descriptions>

            <el-tabs v-model="activeTab" style="margin-top: 20px">
              <el-tab-pane label="仅在文件1" name="onlyInFile1">
                <el-table :data="result.onlyInFile1" height="300">
                  <el-table-column prop="key" label="主键" />
                </el-table>
              </el-tab-pane>
              <el-tab-pane label="仅在文件2" name="onlyInFile2">
                <el-table :data="result.onlyInFile2" height="300">
                  <el-table-column prop="key" label="主键" />
                </el-table>
              </el-tab-pane>
              <el-tab-pane label="已修改" name="modified">
                <el-table :data="result.modified" height="300">
                  <el-table-column prop="key" label="主键" />
                  <el-table-column prop="differences" label="差异字段" />
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </div>
          
          <el-empty v-else description="请先上传文件并开始对比" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 新建配置对话框 -->
    <el-dialog v-model="showConfigDialog" title="新建配置" width="600px">
      <ConfigForm @save="onConfigSave" @cancel="showConfigDialog = false" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from 'axios'
import ConfigForm from './ConfigForm.vue'

const API_BASE = 'http://localhost:8080'

const configs = ref([])
const selectedConfig = ref('')
const file1 = ref(null)
const file2 = ref(null)
const result = ref(null)
const loading = ref(false)
const activeTab = ref('onlyInFile1')
const showConfigDialog = ref(false)

const canCompare = computed(() => {
  return selectedConfig.value && file1.value && file2.value
})

onMounted(() => {
  loadConfigs()
})

const loadConfigs = async () => {
  try {
    const response = await axios.get(`${API_BASE}/api/configs`)
    configs.value = response.data
    if (configs.value.length > 0) {
      selectedConfig.value = configs.value[0]
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

const handleFile1Change = (uploadFile) => {
  file1.value = uploadFile.raw
}

const handleFile2Change = (uploadFile) => {
  file2.value = uploadFile.raw
}

const startCompare = async () => {
  if (!canCompare.value) return
  
  loading.value = true
  result.value = null
  
  try {
    const formData = new FormData()
    formData.append('configName', selectedConfig.value)
    formData.append('file1', file1.value)
    formData.append('file2', file2.value)
    
    const response = await axios.post(`${API_BASE}/api/compare`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    result.value = response.data
  } catch (error) {
    console.error('对比失败:', error)
    alert('对比失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const onConfigSave = () => {
  showConfigDialog.value = false
  loadConfigs()
}
</script>

<style scoped>
.file-comparator {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-section h4 {
  margin: 0 0 10px 0;
  color: #606266;
}

.loading {
  padding: 20px;
}

.result {
  padding: 10px;
}
</style>
