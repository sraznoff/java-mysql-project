insert into project  (project_name, estimated_hours, actual_hours, difficulty, notes)
values ("Test deletion Project", 7.2, 9.6, 5, "Testing to see this is all deletable");
insert into category ( category_name) values ("Test Category");
insert into project_category (project_id, category_id) 
values ((select max(project_id) from project), (select max(category_id) from category));
insert into material (project_id, material_name, num_required, cost) 
values ((select max(project_id) from project), "Test Material", 7, 70.99);
insert into step (project_id, step_text, step_order) 
values ((select max(project_id) from project), "test step text", 1) 
