import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContestProblemsGridComponent } from './contest-problems-grid.component';

describe('ContestProblemsGridComponent', () => {
  let component: ContestProblemsGridComponent;
  let fixture: ComponentFixture<ContestProblemsGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContestProblemsGridComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContestProblemsGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
