import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from './api';
import { useDebounce } from './useDebounce';

export default function ContestsList() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const [page, setPage] = useState(0);
  const [size] = useState(10); 
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 500);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create'); 
  const [formData, setFormData] = useState({ id: null, name: '', startTime: '', duration: '' });

  const loadData = async () => {
    setLoading(true);
    const result = await api.getContests(page, size, debouncedSearch);
    setData(result.content || []);
    setTotalPages(result.totalPages || 0);
    if (page >= result.totalPages && result.totalPages > 0) setPage(0);
    setLoading(false);
  };

  useEffect(() => { loadData(); }, [page, debouncedSearch]);
  useEffect(() => { setPage(0); }, [debouncedSearch]);

  const formatDatePart = (isoString) => {
    if (!isoString) return '';
    return new Date(isoString).toLocaleDateString('ru-RU', {
      day: '2-digit', month: '2-digit', year: 'numeric'
    });
  };

  const formatTimePart = (isoString) => {
    if (!isoString) return '';
    return new Date(isoString).toLocaleTimeString('ru-RU', {
      hour: '2-digit', minute: '2-digit'
    });
  };

  const handleOpenCreate = () => {
    setModalMode('create');
    setFormData({ id: null, name: '', startTime: '', duration: 120 }); // Минуты по умолчанию
    setIsModalOpen(true);
  };

  const handleOpenEdit = (contest) => {
    setModalMode('edit');
    setFormData({ 
      id: contest.id, 
      name: contest.name, 
      startTime: contest.startTime, 
      duration: contest.duration // Берем сырые минуты из бэкенда для формы
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (modalMode === 'create') {
      await api.createContest({ name: formData.name, startTime: formData.startTime, duration: formData.duration });
    } else {
      await api.updateContest(formData.id, { name: formData.name, startTime: formData.startTime, duration: formData.duration });
    }
    setIsModalOpen(false);
    loadData();
  };

  const handleDelete = async (id, name) => {
    if (window.confirm(`Вы уверены, что хотите удалить соревнование "${name}"?`)) {
      await api.deleteContest(id);
      loadData();
    }
  };

  return (
    <div className="p-8 max-w-6xl mx-auto text-left">
      <h1 className="text-2xl font-bold mb-6">Управление соревнованиями</h1>
      
      <div className="flex justify-between items-center mb-4">
        <input 
          type="text" placeholder="Поиск по названию..." value={search} onChange={(e) => setSearch(e.target.value)}
          className="border border-gray-300 p-2 rounded w-96 focus:outline-none focus:border-blue-500" 
        />
        <button 
          onClick={handleOpenCreate}
          className="text-green-600 hover:text-green-800 font-bold hover:underline transition-colors"
        >
          Добавить соревнование
        </button>
      </div>

      <div className="bg-white rounded shadow border border-gray-200 relative min-h-[250px] overflow-hidden">
        {loading && (
          <div className="absolute inset-0 bg-white/70 flex items-center justify-center z-10">
            <span className="text-gray-800 font-bold">Загрузка...</span>
          </div>
        )}
        
        <table className="w-full border-collapse">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="p-4 w-12 text-center text-black font-bold border-r border-gray-200">№</th>
              <th className="p-4 text-center font-bold border-r border-gray-200 text-black">Название</th>
              <th className="p-4 w-40 text-center font-bold border-r border-gray-200 text-black">Начало</th>
              <th className="p-4 w-32 text-center font-bold border-r border-gray-200 text-black">Длительность</th>
              <th className="p-4 text-center w-72 font-bold text-black">Действия</th>
            </tr>
          </thead>
          <tbody>
            {data.map((c, index) => {
              const rowNumber = page * size + index + 1;
              return (
                <tr key={c.id} className="border-b last:border-0 hover:bg-gray-50 transition-colors">
                  <td className="p-4 text-center text-black font-bold font-mono border-r border-gray-200">{rowNumber}</td>
                  <td className="p-4 font-medium text-gray-800 text-left border-r border-gray-200">{c.name}</td>
                  
                  <td className="p-4 text-center text-blue-600 border-r border-gray-200">
                    <div className="font-semibold text-sm">{formatDatePart(c.startTime)}</div>
                    <div className="text-xs font-mono mt-0.5">{formatTimePart(c.startTime)}</div>
                  </td>
                  
                  {/* ИСПОЛЬЗУЕМ ОТФОРМАТИРОВАННУЮ СТРОКУ HH:MM */}
                  <td className="p-4 text-center text-black font-mono font-medium border-r border-gray-200">
                    {c.durationFormatted}
                  </td>
                  
                  <td className="p-4">
                    <div className="flex justify-center gap-5 items-center">
                      <button 
                        onClick={() => handleOpenEdit(c)}
                        className="text-gray-500 hover:text-blue-600 text-sm font-medium hover:underline"
                      >
                        Редактировать
                      </button>
                      
                      <button 
                        onClick={() => handleDelete(c.id, c.name)}
                        className="text-red-500 hover:text-red-700 text-sm font-medium hover:underline"
                      >
                        Удалить
                      </button>

                      <Link 
                        to={`/contests/${c.id}/problems`}
                        className="flex flex-col items-center justify-center text-blue-600 hover:text-blue-800 text-sm font-bold bg-blue-50 hover:bg-blue-100 px-3 py-1 rounded transition-colors"
                      >
                        <span>Задачи</span>
                        <span className="text-lg font-normal leading-none mt-[-2px]">→</span>
                      </Link>
                    </div>
                  </td>
                </tr>
              );
            })}
            {data.length === 0 && !loading && (
              <tr><td colSpan="5" className="p-8 text-center text-gray-500 italic">Соревнования не найдены</td></tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="flex justify-between items-center mt-4 text-sm text-gray-600">
        <div className="font-medium">Страница {totalPages === 0 ? 0 : page + 1} из {totalPages}</div>
        <div className="flex gap-2">
          <button disabled={page === 0} onClick={() => setPage(p => p - 1)} className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-gray-50 transition-colors font-medium">Назад</button>
          <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)} className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-gray-50 transition-colors font-medium">Вперед</button>
        </div>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-xl w-[400px]">
            <h2 className="text-xl font-bold mb-4">
              {modalMode === 'create' ? 'Создать соревнование' : 'Редактировать соревнование'}
            </h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1">Название</label>
                <input 
                  required autoFocus type="text" 
                  value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})}
                  className="w-full border border-gray-300 p-2 rounded focus:outline-none focus:border-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1">Дата и время начала</label>
                <input 
                  required type="datetime-local" 
                  value={formData.startTime} onChange={(e) => setFormData({...formData, startTime: e.target.value})}
                  className="w-full border border-gray-300 p-2 rounded focus:outline-none focus:border-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1">Длительность (в минутах)</label>
                <input 
                  required type="number" min="1"
                  value={formData.duration} onChange={(e) => setFormData({...formData, duration: e.target.value})}
                  className="w-full border border-gray-300 p-2 rounded focus:outline-none focus:border-blue-500"
                />
              </div>
              <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-4 py-2 text-gray-600 font-medium hover:bg-gray-100 rounded">
                  Отмена
                </button>
                <button type="submit" className="px-4 py-2 bg-blue-600 text-white font-bold rounded hover:bg-blue-700">
                  Сохранить
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}