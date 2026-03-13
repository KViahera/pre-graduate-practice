import { Routes } from '@angular/router';
import { ContestsListComponent } from './components/contests-list/contests-list.component';
import { ContestProblemsGridComponent } from './components/contest-problems-grid/contest-problems-grid.component';

export const routes: Routes = [
  { path: '', component: ContestsListComponent },
  { path: 'contests/:contestId/problems', component: ContestProblemsGridComponent }
];