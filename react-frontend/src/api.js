const API_URL = 'http://localhost:8080/api';

// --- ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ---
const indexToLetter = (index) => String.fromCharCode(65 + index);

const minutesToHHMM = (min) => {
  if (!min) return "00:00";
  const h = String(Math.floor(min / 60)).padStart(2, '0');
  const m = String(min % 60).padStart(2, '0');
  return `${h}:${m}`;
};

const mapProblemToUI = (backendProblem, index = 0) => {
  const samples = backendProblem.testCases?.filter(tc => tc.isSample).map(tc => ({
    input: tc.inputData || '',
    output: tc.outputData || ''
  })) || [];

  if (samples.length === 0) {
    samples.push({ input: '', output: '' });
  }

  return {
    id: backendProblem.id,
    name: backendProblem.title,
    timeLimit: `${backendProblem.timeLimitMilliseconds} мс`,
    memoryLimit: `${backendProblem.memoryLimitMegabytes} МБ`,
    legend: backendProblem.statement,
    inputFormat: backendProblem.inputFormat,
    outputFormat: backendProblem.outputFormat,
    samples: samples,
    number: indexToLetter(index),
    usageCount: backendProblem.usageCount || 0
  };
};

const mapUIToProblemDTO = (formData) => {
  return {
    title: formData.name,
    timeLimitMilliseconds: parseInt(formData.timeLimit) || 1000,
    memoryLimitMegabytes: parseInt(formData.memoryLimit) || 256,
    statement: formData.legend,
    inputFormat: formData.inputFormat,
    outputFormat: formData.outputFormat,
    testCases: formData.samples.map(s => ({
      inputData: s.input,
      outputData: s.output,
      isSample: true
    }))
  };
};

export const api = {
  // --- КОНТЕСТЫ ---
  getContests: async (page = 0, size = 10, search = '') => {
    const params = new URLSearchParams({ page, size, search });
    const response = await fetch(`${API_URL}/contests?${params}`);
    const data = await response.json();
    // Сохраняем duration (минуты) для формы, а durationFormatted для таблицы
    if (data.content) {
      data.content = data.content.map(c => ({ 
        ...c, 
        durationFormatted: minutesToHHMM(c.duration) 
      }));
    }
    return data;
  },

  getContest: async (id) => {
    const response = await fetch(`${API_URL}/contests/${id}`);
    const data = await response.json();
    return { ...data, durationFormatted: minutesToHHMM(data.duration) };
  },

  createContest: async (data) => {
    // Форма отдает минуты напрямую
    const body = { ...data, duration: parseInt(data.duration) || 0 };
    const response = await fetch(`${API_URL}/contests`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    return response.json();
  },

  updateContest: async (id, data) => {
    const body = { ...data, duration: parseInt(data.duration) || 0 };
    await fetch(`${API_URL}/contests/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
  },

  deleteContest: async (id) => {
    await fetch(`${API_URL}/contests/${id}`, { method: 'DELETE' });
  },

  // --- ЗАДАЧИ В КОНТЕСТЕ ---
  getContestProblems: async (contestId) => {
    const response = await fetch(`${API_URL}/contests/${contestId}/problems`);
    if (!response.ok) return { content: [] };
    const list = await response.json();
    const mappedContent = list.map(cp => mapProblemToUI(cp.problem, cp.index));
    return {
      content: mappedContent,
      totalPages: 1, 
      totalElements: mappedContent.length
    };
  },

  createAndLinkProblem: async (contestId, formData) => {
    const body = mapUIToProblemDTO(formData);
    const response = await fetch(`${API_URL}/contests/${contestId}/problems`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    return response.json();
  },

  linkExistingProblem: async (contestId, problemId) => {
    await fetch(`${API_URL}/contests/${contestId}/problems/link/${problemId}`, { method: 'POST' });
  },

  reorderContestProblems: async (contestId, problemIds) => {
    await fetch(`${API_URL}/contests/${contestId}/problems/reorder`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(problemIds)
    });
  },

  unlinkProblem: async (contestId, problemId) => {
    await fetch(`${API_URL}/contests/${contestId}/problems/${problemId}`, { method: 'DELETE' });
  },

  // --- ГЛОБАЛЬНЫЙ АРХИВ ---
  getGlobalProblems: async (search = '') => {
    const response = await fetch(`${API_URL}/problems?search=${search}`);
    const list = await response.json();
    return list.map(p => mapProblemToUI(p));
  },

  updateGlobalProblem: async (problemId, formData) => {
    const body = mapUIToProblemDTO(formData);
    await fetch(`${API_URL}/problems/${problemId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
  },

  deleteProblemCompletely: async (problemId) => {
    await fetch(`${API_URL}/problems/${problemId}`, { method: 'DELETE' });
  }
};