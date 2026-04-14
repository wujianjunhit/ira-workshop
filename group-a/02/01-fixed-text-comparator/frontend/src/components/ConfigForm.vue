<template>
  <el-form :model="form" label-width="120px">
    <el-form-item label="配置名称">
      <el-input v-model="form.name" placeholder="请输入配置名称" />
    </el-form-item>
    
    <el-form-item label="描述">
      <el-input v-model="form.description" type="textarea" placeholder="请输入配置描述" />
    </el-form-item>
    
    <el-divider>字段定义</el-divider>
    
    <div v-for="(field, index) in form.fields" :key="index" class="field-item">
      <el-row :gutter="10">
        <el-col :span="6">
          <el-input v-model="field.name" placeholder="字段名" />
        </el-col>
        <el-col :span="4">
          <el-input-number v-model="field.start" :min="0" placeholder="起始位置" />
        </el-col>
        <el-col :span="4">
          <el-input-number v-model="field.length" :min="1" placeholder="长度" />
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="field.isKey">主键</el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-button type="danger" @click="removeField(index)">删除</el-button>
        </el-col>
      </el-row>
    </div>
    
    <el-button type="primary" @click="addField" style="margin-top: 10px">添加字段</el-button>
    
    <el-divider />
    
    <el-form-item>
      <el-button type="primary" @click="saveConfig">保存</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive } from 'vue'
import axios from 'axios'

const API_BASE = 'http://localhost:8080'

const emit = defineEmits(['save', 'cancel'])

const form = reactive({
  name: '',
  description: '',
  fields: []
})

const addField = () => {
  form.fields.push({
    name: '',
    start: 0,
    length: 10,
    isKey: false
  })
}

const removeField = (index) => {
  form.fields.splice(index, 1)
}

const saveConfig = async () => {
  try {
    const keyFields = form.fields.filter(f => f.isKey).map(f => f.name)
    
    const config = {
      name: form.name,
      description: form.description,
      fields: form.fields.map(f => ({
        name: f.name,
        start: f.start,
        length: f.length
      })),
      keyFields: keyFields
    }
    
    await axios.post(`${API_BASE}/api/configs`, config)
    emit('save')
  } catch (error) {
    console.error('保存配置失败:', error)
    alert('保存配置失败: ' + (error.response?.data?.message || error.message))
  }
}
</script>

<style scoped>
.field-item {
  margin-bottom: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
