import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-contests-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  providers: [DatePipe],
  templateUrl: './contests-list.component.html'
})
export class ContestsListComponent implements OnInit, OnDestroy {
  private api = inject(ApiService);
  
  data: any[] = [];
  loading = false;
  page = 0;
  size = 10;
  totalPages = 0;
  
  search = '';
  searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  isModalOpen = false;
  modalMode: 'create' | 'edit' = 'create';
  formData = { id: null, name: '', startTime: '', duration: 120 };

  ngOnInit() {
    this.loadData();
    this.searchSubject.pipe(debounceTime(500), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(term => { this.page = 0; this.loadData(term); });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearchChange(value: string) {
    this.search = value;
    this.searchSubject.next(value);
  }

  loadData(searchTerm = this.search) {
    this.loading = true;
    this.api.getContests(this.page, this.size, searchTerm).subscribe({
      next: (res) => {
        this.data = res.content || [];
        this.totalPages = res.totalPages || 0;
        if (this.page >= res.totalPages && res.totalPages > 0) this.page = 0;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  changePage(delta: number) { this.page += delta; this.loadData(); }

  handleOpenCreate() {
    this.modalMode = 'create';
    this.formData = { id: null, name: '', startTime: '', duration: 120 };
    this.isModalOpen = true;
  }

  handleOpenEdit(contest: any) {
    this.modalMode = 'edit';
    this.formData = { ...contest };
    this.isModalOpen = true;
  }

  handleSave() {
    const action = this.modalMode === 'create' ? this.api.createContest(this.formData) : this.api.updateContest(this.formData.id!, this.formData);
    action.subscribe(() => { this.isModalOpen = false; this.loadData(); });
  }

  handleDelete(id: string, name: string) {
    if (confirm(`Вы уверены, что хотите удалить соревнование "${name}"?`)) {
      this.api.deleteContest(id).subscribe(() => this.loadData());
    }
  }
}