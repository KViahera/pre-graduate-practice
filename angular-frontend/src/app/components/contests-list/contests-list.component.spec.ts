import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContestsListComponent } from './contests-list.component';

describe('ContestsListComponent', () => {
  let component: ContestsListComponent;
  let fixture: ComponentFixture<ContestsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContestsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContestsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
