import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';

const EMPTY_PROBLEM = { name: '', timeLimit: '1000', memoryLimit: '256', legend: '', inputFormat: '', outputFormat: '', samples: [{ input: '', output: '' }] };

@Component({
  selector: 'app-contest-problems-grid',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './contest-problems-grid.component.html'
})
export class ContestProblemsGridComponent implements OnInit, OnDestroy {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  
  contestId = this.route.snapshot.paramMap.get('contestId') || '';
  contestName = 'Загрузка...';
  
  data: any[] = [];
  loading = false;
  search = '';
  searchSubject = new Subject<string>();
  
  globalProblems: any[] = [];
  globalSearch = '';
  globalSearchSubject = new Subject<string>();
  
  private destroy$ = new Subject<void>();

  isAddModalOpen = false;
  addTab: 'new' | 'existing' = 'new';
  formData = JSON.parse(JSON.stringify(EMPTY_PROBLEM));
  isEditMode = false;
  editingProblemId: string | null = null;
  draggedIndex: number | null = null;

  ngOnInit() {
    this.api.getContest(this.contestId).subscribe(c => this.contestName = c ? c.name : 'Неизвестный контест');
    this.loadData();

    this.searchSubject.pipe(debounceTime(500), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(term => this.loadData(term));

    this.globalSearchSubject.pipe(debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(term => { if (this.isAddModalOpen && this.addTab === 'existing') this.loadGlobalProblems(term); });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearchChange(v: string) { this.search = v; this.searchSubject.next(v); }
  onGlobalSearchChange(v: string) { this.globalSearch = v; this.globalSearchSubject.next(v); }

  loadData(searchTerm = this.search) {
    this.loading = true;
    this.api.getContestProblems(this.contestId, 0, 10, searchTerm).subscribe({
      next: (res) => { this.data = res.content || []; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadGlobalProblems(term = this.globalSearch) {
    this.api.getGlobalProblems(term).subscribe(res => this.globalProblems = res);
  }

  handleDragStart(event: DragEvent, index: number) { this.draggedIndex = index; }
  handleDragOver(event: DragEvent) { event.preventDefault(); }
  handleDrop(event: DragEvent, dropIndex: number) {
    if (this.draggedIndex === null || this.draggedIndex === dropIndex) return;
    const newData = [...this.data];
    const draggedItem = newData[this.draggedIndex];
    newData.splice(this.draggedIndex, 1);
    newData.splice(dropIndex, 0, draggedItem);
    
    this.data = newData.map((item, idx) => ({ ...item, number: String.fromCharCode(65 + idx) }));
    this.draggedIndex = null;
    this.api.reorderContestProblems(this.contestId, this.data.map(item => item.id)).subscribe();
  }

  handleOpenAddModal() {
    this.isEditMode = false;
    this.addTab = 'new';
    this.formData = JSON.parse(JSON.stringify(EMPTY_PROBLEM));
    this.isAddModalOpen = true;
  }

  setTab(tab: 'new' | 'existing') {
    this.addTab = tab;
    if (tab === 'existing') this.loadGlobalProblems();
  }

  handleOpenEditModal(problem: any) {
    this.isEditMode = true;
    this.editingProblemId = problem.id;
    this.formData = JSON.parse(JSON.stringify(problem));
    this.isAddModalOpen = true;
  }

  handleSaveForm() {
    const action = this.isEditMode ? this.api.updateGlobalProblem(this.editingProblemId!, this.formData) : this.api.createAndLinkProblem(this.contestId, this.formData);
    action.subscribe(() => { this.isAddModalOpen = false; this.loadData(); });
  }

  handleLinkExisting(problemId: string) {
    this.api.linkExistingProblem(this.contestId, problemId).subscribe({
      next: () => { this.isAddModalOpen = false; this.loadData(); },
      error: (err) => alert('Ошибка привязки')
    });
  }

  handleUnlink(problemId: string, name: string) {
    if (confirm(`Отвязать задачу "${name}" от контеста?`)) {
      this.api.unlinkProblem(this.contestId, problemId).subscribe(() => this.loadData());
    }
  }

  handleDeleteCompletely(problemId: string, name: string) {
    if (confirm(`Внимание! Задача удалится везде!\n\nУдалить "${name}"?`)) {
      this.api.deleteProblemCompletely(problemId).subscribe(() => this.loadData());
    }
  }

  addSample() { this.formData.samples.push({ input: '', output: '' }); }
  removeSample(index: number) { this.formData.samples.splice(index, 1); }
}