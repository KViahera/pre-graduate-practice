import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

const API_URL = 'http://localhost:8080/api';

const indexToLetter = (index: number) => String.fromCharCode(65 + index);

const minutesToHHMM = (min: number) => {
  if (!min) return "00:00";
  const h = String(Math.floor(min / 60)).padStart(2, '0');
  const m = String(min % 60).padStart(2, '0');
  return `${h}:${m}`;
};

const mapProblemToUI = (backendProblem: any, index = 0) => {
  const samples = backendProblem.testCases?.filter((tc: any) => tc.isSample).map((tc: any) => ({
    input: tc.inputData || '',
    output: tc.outputData || ''
  })) || [];

  if (samples.length === 0) samples.push({ input: '', output: '' });

  return {
    id: backendProblem.id,
    name: backendProblem.title,
    timeLimit: `${backendProblem.timeLimitMilliseconds}`,
    memoryLimit: `${backendProblem.memoryLimitMegabytes}`,
    legend: backendProblem.statement,
    inputFormat: backendProblem.inputFormat,
    outputFormat: backendProblem.outputFormat,
    samples: samples,
    number: indexToLetter(index),
    usageCount: backendProblem.usageCount || 0
  };
};

const mapEntityToDto = (formData: any) => {
  return {
    title: formData.name,
    timeLimitMilliseconds: parseInt(formData.timeLimit) || 1000,
    memoryLimitMegabytes: parseInt(formData.memoryLimit) || 256,
    statement: formData.legend,
    inputFormat: formData.inputFormat,
    outputFormat: formData.outputFormat,
    testCases: formData.samples.map((s: any) => ({
      inputData: s.input,
      outputData: s.output,
      isSample: true
    }))
  };
};

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  // --- КОНТЕСТЫ ---
  getContests(page = 0, size = 10, search = ''): Observable<any> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);

    return this.http.get<any>(`${API_URL}/contests`, { params }).pipe(
      map(data => {
        if (data.content) {
          data.content = data.content.map((c: any) => ({ ...c, durationFormatted: minutesToHHMM(c.duration) }));
        }
        return data;
      })
    );
  }

  getContest(id: string): Observable<any> {
    return this.http.get<any>(`${API_URL}/contests/${id}`).pipe(
      map(data => ({ ...data, durationFormatted: minutesToHHMM(data.duration) }))
    );
  }

  createContest(data: any): Observable<any> {
    const body = { ...data, duration: parseInt(data.duration) || 0 };
    return this.http.post(`${API_URL}/contests`, body);
  }

  updateContest(id: string, data: any): Observable<any> {
    const body = { ...data, duration: parseInt(data.duration) || 0 };
    return this.http.put(`${API_URL}/contests/${id}`, body);
  }

  deleteContest(id: string): Observable<any> {
    return this.http.delete(`${API_URL}/contests/${id}`);
  }

  // --- ЗАДАЧИ В КОНТЕСТЕ ---
  getContestProblems(contestId: string, page = 0, size = 10, search = ''): Observable<any> {
    return this.http.get<any[]>(`${API_URL}/contests/${contestId}/problems`).pipe(
      map(list => {
        const mappedContent = list.map((cp: any) => mapProblemToUI(cp.problem, cp.index));
        // Локальная фильтрация, если бэк не поддерживает поиск
        const filtered = search ? mappedContent.filter(p => p.name.toLowerCase().includes(search.toLowerCase())) : mappedContent;
        return { content: filtered, totalPages: 1, totalElements: filtered.length };
      })
    );
  }

  createAndLinkProblem(contestId: string, formData: any): Observable<any> {
    return this.http.post(`${API_URL}/contests/${contestId}/problems`, mapEntityToDto(formData));
  }

  linkExistingProblem(contestId: string, problemId: string): Observable<any> {
    return this.http.post(`${API_URL}/contests/${contestId}/problems/link/${problemId}`, {});
  }

  reorderContestProblems(contestId: string, problemIds: string[]): Observable<any> {
    return this.http.patch(`${API_URL}/contests/${contestId}/problems/reorder`, problemIds);
  }

  unlinkProblem(contestId: string, problemId: string): Observable<any> {
    return this.http.delete(`${API_URL}/contests/${contestId}/problems/${problemId}`);
  }

  // --- ГЛОБАЛЬНЫЙ АРХИВ ---
  getGlobalProblems(search = ''): Observable<any> {
    return this.http.get<any[]>(`${API_URL}/problems?search=${search}`).pipe(
      map(list => list.map(p => mapProblemToUI(p)))
    );
  }

  updateGlobalProblem(problemId: string, formData: any): Observable<any> {
    return this.http.put(`${API_URL}/problems/${problemId}`, mapEntityToDto(formData));
  }

  deleteProblemCompletely(problemId: string): Observable<any> {
    return this.http.delete(`${API_URL}/problems/${problemId}`);
  }
}