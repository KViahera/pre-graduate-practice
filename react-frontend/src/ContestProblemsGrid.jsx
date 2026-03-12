import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from './api';
import { useDebounce } from './useDebounce';

const EMPTY_PROBLEM = { 
  name: '', timeLimit: '1000', memoryLimit: '256', 
  legend: '', inputFormat: '', outputFormat: '', 
  samples: [{ input: '', output: '' }] 
};

export default function ContestProblemsGrid() {
  const { contestId } = useParams();
  const [contestName, setContestName] = useState('Загрузка...');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 500);

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [addTab, setAddTab] = useState('new');
  const [formData, setFormData] = useState(EMPTY_PROBLEM);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingProblemId, setEditingProblemId] = useState(null);

  const [globalProblems, setGlobalProblems] = useState([]);
  const [globalSearch, setGlobalSearch] = useState('');
  const debouncedGlobalSearch = useDebounce(globalSearch, 400);
  const [draggedIndex, setDraggedIndex] = useState(null);

  useEffect(() => { 
    api.getContest(contestId).then(c => setContestName(c ? c.name : 'Неизвестный контест')); 
  }, [contestId]);

  const loadData = async () => {
    setLoading(true);
    const result = await api.getContestProblems(contestId, page, 10, debouncedSearch);
    setData(result.content || []);
    setTotalPages(result.totalPages || 0);
    setLoading(false);
  };

  useEffect(() => { loadData(); }, [page, debouncedSearch, contestId]);

  useEffect(() => {
    if (isAddModalOpen && addTab === 'existing' && !isEditMode) {
      api.getGlobalProblems(debouncedGlobalSearch).then(setGlobalProblems);
    }
  }, [isAddModalOpen, addTab, debouncedGlobalSearch, isEditMode]);

  const handleDragStart = (index) => setDraggedIndex(index);
  const handleDragOver = (e) => e.preventDefault();
  const handleDrop = async (dropIndex) => {
    if (draggedIndex === null || draggedIndex === dropIndex) return;
    const newData = [...data];
    const draggedItem = newData[draggedIndex];
    newData.splice(draggedIndex, 1);
    newData.splice(dropIndex, 0, draggedItem);
    
    const optimisticallyUpdated = newData.map((item, idx) => ({
      ...item,
      number: String.fromCharCode(65 + idx)
    }));
    setData(optimisticallyUpdated);
    setDraggedIndex(null);
    
    await api.reorderContestProblems(contestId, optimisticallyUpdated.map(item => item.id));
  };

  const handleOpenAddModal = () => {
    setIsEditMode(false);
    setAddTab('new');
    setFormData(EMPTY_PROBLEM);
    setIsAddModalOpen(true);
  };

  const handleOpenEditModal = (problem) => {
    setIsEditMode(true);
    setEditingProblemId(problem.id);
    setFormData({ ...problem });
    setIsAddModalOpen(true);
  };

  const handleSaveForm = async (e) => {
    e.preventDefault();
    if (isEditMode) {
      await api.updateGlobalProblem(editingProblemId, formData);
    } else {
      await api.createAndLinkProblem(contestId, formData);
    }
    setIsAddModalOpen(false);
    loadData();
  };

  const handleLinkExisting = async (problemId) => {
    try {
      await api.linkExistingProblem(contestId, problemId);
      setIsAddModalOpen(false);
      loadData();
    } catch (err) { alert(err.message); }
  };

  const handleUnlink = async (problemId, name) => {
    if (window.confirm(`Вы действительно хотите отвязать задачу "${name}" от текущего контеста?`)) {
      await api.unlinkProblem(contestId, problemId);
      loadData();
    }
  };

  const handleDeleteCompletely = async (problemId, name) => {
    if (window.confirm(`Внимание! Задача удалится из всех остальных, связанных с ней, соревнований!\n\nВы действительно хотите удалить задачу "${name}" из текущего контеста?`)) {
      await api.deleteProblemCompletely(problemId);
      loadData();
    }
  };

  const handleSampleChange = (index, field, value) => {
    const newSamples = [...formData.samples];
    newSamples[index][field] = value;
    setFormData({ ...formData, samples: newSamples });
  };

  const addSample = () => {
    setFormData({ ...formData, samples: [...formData.samples, { input: '', output: '' }] });
  };

  const removeSample = (index) => {
    const newSamples = formData.samples.filter((_, i) => i !== index);
    setFormData({ ...formData, samples: newSamples });
  };

  return (
    <div className="p-8 max-w-6xl mx-auto text-left">
      <div className="flex items-center gap-4 mb-6">
        <Link to="/" className="text-gray-500 hover:text-black transition-colors">← К списку соревнований</Link>
        <h1 className="text-2xl font-bold">{contestName}</h1>
      </div>

      <div className="flex justify-between items-center mb-4 w-full">
        <input 
          type="text" placeholder="Поиск по названию..." value={search} onChange={(e) => setSearch(e.target.value)}
          className="border border-gray-300 p-2 rounded w-96 focus:outline-none focus:border-blue-500"
        />

        <button 
          onClick={handleOpenAddModal} 
          className="text-green-600 hover:text-green-800 font-bold hover:underline transition-colors"
        >
          Добавить задачу
        </button>
      </div>

      <div className="bg-white rounded shadow border border-gray-200 relative overflow-hidden min-h-[200px]">
        {loading && (
          <div className="absolute inset-0 bg-white/70 flex items-center justify-center z-10">
            <span className="text-gray-800 font-bold">Загрузка...</span>
          </div>
        )}

        <table className="w-full border-collapse table-fixed">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="p-4 w-12 text-center text-black font-bold border-r border-gray-200"></th>
              <th className="p-4 w-24 text-center text-black font-bold border-r border-gray-200">№</th>
              <th className="p-4 text-center text-black font-bold border-r border-gray-200">Название</th>
              <th className="p-4 text-center text-black font-bold w-72">Действия</th>
            </tr>
          </thead>
          <tbody>
            {data.map((row, index) => (
              <tr 
                key={row.id} draggable={!search}
                onDragStart={() => handleDragStart(index)} onDragOver={handleDragOver} onDrop={() => handleDrop(index)}
                className={`border-b last:border-0 hover:bg-gray-50 transition-colors ${!search ? 'cursor-grab' : ''} ${draggedIndex === index ? 'opacity-30' : ''}`}
              >
                <td className="p-4 text-center text-gray-400 select-none border-r border-gray-200">
                  {!search && "⋮⋮"}
                </td>
                
                <td className="p-4 text-center text-black font-bold font-mono border-r border-gray-200">
                  {row.number}
                </td>
                
                <td className="p-4 text-left border-r border-gray-200 overflow-hidden">
                  <span className="font-medium text-gray-800 block truncate">{row.name}</span>
                  {row.usageCount > 1 && <span className="text-xs text-orange-600 font-medium">Используется в {row.usageCount} контестах</span>}
                </td>
                
                <td className="p-4 text-center w-72">
                  <div className="flex justify-center gap-5 items-center">
                    <button 
                      onClick={() => handleOpenEditModal(row)} 
                      className="text-gray-500 hover:text-blue-600 text-sm font-medium hover:underline"
                    >
                      Редактировать
                    </button>
                    <button 
                      onClick={() => handleUnlink(row.id, row.name)} 
                      className="text-orange-500 hover:text-orange-700 text-sm font-medium hover:underline"
                    >
                      Отвязать
                    </button>
                    <button 
                      onClick={() => handleDeleteCompletely(row.id, row.name)} 
                      className="text-red-500 hover:text-red-700 text-sm font-medium hover:underline"
                    >
                      Удалить
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {data.length === 0 && !loading && (
              <tr><td colSpan="4" className="p-8 text-center text-gray-500 italic">Задач пока нет</td></tr>
            )}
          </tbody>
        </table>
      </div>

      {isAddModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] flex flex-col border border-gray-300">
            
            <div className="p-6 border-b border-gray-200 bg-white rounded-t-lg">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold text-gray-800">{isEditMode ? 'Редактирование задачи' : 'Добавление задачи'}</h2>
                <button onClick={() => setIsAddModalOpen(false)} className="text-gray-400 hover:text-black font-bold text-xl transition-colors">✕</button>
              </div>
              {!isEditMode && (
                <div className="flex gap-6 border-b border-gray-200">
                  <button onClick={() => setAddTab('new')} className={`pb-2 font-medium transition-colors ${addTab === 'new' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500 hover:text-gray-800'}`}>Создать новую</button>
                  <button onClick={() => setAddTab('existing')} className={`pb-2 font-medium transition-colors ${addTab === 'existing' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500 hover:text-gray-800'}`}>Выбрать из Архива</button>
                </div>
              )}
            </div>

            <div className="p-6 overflow-y-auto flex-grow bg-white">
              
              {/* ВЕРНУЛ ЖЕЛТЫЙ БЛОК БЕЗ ИКОНКИ */}
              {isEditMode && (
                <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6 rounded-r-md">
                  <h3 className="text-sm font-bold text-yellow-800 text-left">Внимание!</h3>
                  <div className="mt-1 text-sm text-yellow-700 text-left leading-relaxed">
                    При сохранении изменений, они также применяться ко всем остальным, связанным с текущей задачей, соревнованиям.
                  </div>
                </div>
              )}

              {(isEditMode || addTab === 'new') && (
                <form id="problemForm" onSubmit={handleSaveForm} className="space-y-6 text-left">
                  <div>
                    <label className="block text-sm font-bold text-black mb-1">Название задачи</label>
                    <input required value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} className="w-full border border-gray-300 p-2 rounded focus:outline-none focus:border-blue-500" />
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-bold text-black mb-1">Ограничение по времени (мс)</label>
                      <input required value={formData.timeLimit} onChange={e => setFormData({...formData, timeLimit: e.target.value})} className="w-full border border-gray-300 p-2 rounded outline-none focus:border-blue-500" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-black mb-1">Ограничение по памяти (МБ)</label>
                      <input required value={formData.memoryLimit} onChange={e => setFormData({...formData, memoryLimit: e.target.value})} className="w-full border border-gray-300 p-2 rounded outline-none focus:border-blue-500" />
                    </div>
                  </div>
                  
                  <div>
                    <label className="block text-sm font-bold text-black mb-1">Условие</label>
                    <textarea 
                      required rows="5" 
                      placeholder="Поддерживается LaTeX. Например: дан массив $A$ длины $N$..."
                      value={formData.legend} onChange={e => setFormData({...formData, legend: e.target.value})} 
                      className="w-full border border-gray-300 p-2 rounded text-sm resize-y focus:outline-none focus:border-blue-500" 
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold text-black mb-1">Формат входных данных</label>
                    <textarea 
                      required rows="3" 
                      placeholder="Поддерживается LaTeX. Например: в первой строке вводится число $N \le 10^5$..."
                      value={formData.inputFormat} onChange={e => setFormData({...formData, inputFormat: e.target.value})} 
                      className="w-full border border-gray-300 p-2 rounded text-sm focus:outline-none focus:border-blue-500" 
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold text-black mb-1">Формат выходных данных</label>
                    <textarea 
                      required rows="3" 
                      placeholder="Поддерживается LaTeX. Например: выведите $\sum_{i=1}^N A_i$..."
                      value={formData.outputFormat} onChange={e => setFormData({...formData, outputFormat: e.target.value})} 
                      className="w-full border border-gray-300 p-2 rounded text-sm focus:outline-none focus:border-blue-500" 
                    />
                  </div>
                  
                  <div className="space-y-4">
                    <div className="border-b border-gray-200 pb-2">
                      <h3 className="text-sm font-bold text-black">Примеры</h3>
                    </div>
                    
                    <div className="space-y-3">
                      {formData.samples.map((sample, index) => (
                        <div key={index} className="flex items-center gap-3">
                          <div className="flex-grow bg-gray-50 border border-gray-200 rounded p-4">
                            <div className="grid grid-cols-2 gap-4">
                              <textarea 
                                required placeholder="Ввод..." rows="3" 
                                value={sample.input} onChange={e => handleSampleChange(index, 'input', e.target.value)} 
                                className="w-full border border-gray-300 focus:border-blue-500 p-2 rounded font-mono text-sm outline-none" 
                              />
                              <textarea 
                                required placeholder="Вывод..." rows="3" 
                                value={sample.output} onChange={e => handleSampleChange(index, 'output', e.target.value)} 
                                className="w-full border border-gray-300 focus:border-blue-500 p-2 rounded font-mono text-sm outline-none" 
                              />
                            </div>
                          </div>
                          
                          <button 
                            type="button" 
                            onClick={() => removeSample(index)} 
                            className="w-8 h-8 shrink-0 flex items-center justify-center rounded-full text-gray-400 hover:bg-red-50 hover:text-red-500 transition-colors text-2xl font-medium leading-none pb-0.5"
                            title="Удалить пример"
                          >
                            −
                          </button>
                        </div>
                      ))}
                    </div>

                    {formData.samples.length === 0 && (
                      <p className="text-sm text-gray-500 italic text-center">Примеры не добавлены.</p>
                    )}

                    <div className="flex justify-center mt-2">
                      <button 
                        type="button" 
                        onClick={addSample} 
                        className="w-8 h-8 flex items-center justify-center rounded-full bg-transparent text-gray-400 hover:bg-green-50 hover:text-green-600 transition-colors text-2xl font-medium pb-0.5"
                        title="Добавить пример"
                      >
                        +
                      </button>
                    </div>
                  </div>
                </form>
              )}

              {!isEditMode && addTab === 'existing' && (
                <div className="space-y-4 text-left">
                  <input 
                    type="text" placeholder="Поиск в архиве..." value={globalSearch} onChange={e => setGlobalSearch(e.target.value)} 
                    className="w-full border border-gray-300 p-2 rounded outline-none focus:border-blue-500" 
                  />
                  <div className="bg-white rounded border border-gray-200 max-h-96 overflow-y-auto">
                    {globalProblems.map(p => (
                      <div key={p.id} className="flex justify-between items-center p-4 border-b last:border-0 hover:bg-gray-50 transition-colors">
                        <div>
                          <span className="font-bold text-gray-800 block">{p.name}</span>
                          <p className="text-xs text-gray-500">{p.timeLimit} • {p.memoryLimit}</p>
                        </div>
                        <button 
                          onClick={() => handleLinkExisting(p.id)} 
                          className="bg-blue-600 text-white px-5 py-2 rounded text-sm font-bold hover:bg-blue-700 shadow-sm transition-all active:scale-95"
                        >
                          Добавить
                        </button>
                      </div>
                    ))}
                    {globalProblems.length === 0 && (
                      <div className="p-12 text-center text-gray-500 italic">Задачи не найдены</div>
                    )}
                  </div>
                </div>
              )}
            </div>

            {(isEditMode || addTab === 'new') && (
              <div className="p-4 border-t bg-gray-50 flex justify-end gap-3 rounded-b-lg">
                <button type="button" onClick={() => setIsAddModalOpen(false)} className="px-4 py-2 text-gray-600 font-medium hover:bg-gray-200 rounded transition-colors">Отмена</button>
                <button type="submit" form="problemForm" className="px-4 py-2 bg-blue-600 text-white font-bold rounded hover:bg-blue-700 shadow-sm transition-all active:scale-95">Сохранить</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}