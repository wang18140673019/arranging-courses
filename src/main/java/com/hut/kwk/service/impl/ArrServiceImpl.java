package com.hut.kwk.service.impl;

import com.hut.kwk.constant.ServerResponse;
import com.hut.kwk.model.entity.*;
import com.hut.kwk.model.mapper.*;
import com.hut.kwk.service.IArrService;
import com.hut.kwk.util.DayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Create by Wang Heng on 2019-04-24
 *
 * @author Wang Heng
 */
@Service
public class ArrServiceImpl implements IArrService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ClassesMapper classesMapper;
    @Autowired
    private ClassroomMapper classroomMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseTableMapper courseTableMapper;
    @Autowired
    private ArrangeMapper arrangeMapper;

    @Override
    public ServerResponse<String> arr() {
        //查出所有 需要排课的课程安排 和 教室
        //todo 现在是全部查出来 条件后面可以修改

        //需要排的课
        List<Arrange> arranges = arrangeMapper.selectByExample(new ArrangeQuery());

        //教室
        List<Classroom> classrooms = classroomMapper.selectByExample(new ClassroomQuery());

        List<CourseTable> tables = handle(arranges, classrooms, 20);

        courseTableMapper.deleteByExample(new CourseTableQuery());

        int count = courseTableMapper.batchInsert(tables);

        if (count >0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    private  List<CourseTable> handle(List<Arrange> arranges, List<Classroom> classrooms, int weeks) {
        List<CourseTable> courseTables = new ArrayList<>();

        ArrayList<ClassroomHelp> helps = new ArrayList<>();
        for (Classroom c : classrooms) {
            for (int x = 1; x <= 5; x++) {
                for (int y = 1; y <= 4; y++) {
                    helps.add(toHelp(c, x, y));
                }
            }
        }

        Random ra = new Random();
        int temp;

        for (Arrange arrange :arranges){
            Integer number = Integer.valueOf(classesMapper.selectByPrimaryKey(arrange.getClassId()).getClassNumber());

            temp = ra.nextInt(helps.size()-1);
            int flag = temp;
            boolean index = true;

            for (int x=0;x<helps.size();x++){
                ClassroomHelp classroom = helps.get(temp);
                if (classroom.getRoomSpace()>= number){
                    courseTables.add(toTabel(arrange,classroom));
                    helps.remove(x);
                    break;
                }else {
                    System.out.println(arrange.toString());
                }
            }
        }

        return courseTables;

    }

    private CourseTable toTabel(Arrange arrange,ClassroomHelp classroomHelp){
        CourseTable courseTable = new CourseTable();
        courseTable.setArrId(arrange.getId());
        courseTable.setClassId(arrange.getClassId());
        courseTable.setRoomId(classroomHelp.getId());
        courseTable.setTecherId(arrange.getTecherId());
        courseTable.setCourseId(arrange.getCourseId());
        courseTable.setTimeId(classroomHelp.getNumber());
        courseTable.setTimeName(DayUtil.toDay(classroomHelp.getDay()));
        //todo
        courseTable.setSemeId(0);
        courseTable.setSemeName("第一学期");

        courseTable.setClassName(arrange.getClassName());
        courseTable.setCourseName(arrange.getCourseName());
        courseTable.setRoomName(classroomHelp.getRoomName());
        courseTable.setTecherName(arrange.getTecherName());
        courseTable.setTemporary(0);
        courseTable.setStatu(0);
        courseTable.setMark("无");
        return courseTable;
    }

    private ClassroomHelp toHelp(Classroom classroom, Integer x, Integer y) {
        ClassroomHelp classroomHelp = new ClassroomHelp();
        classroomHelp.setId(classroom.getId());
        classroomHelp.setRoomName(classroom.getRoomName());
        classroomHelp.setRoomLayer(classroom.getRoomLayer());
        classroomHelp.setRoomSign(classroom.getRoomSign());
        classroomHelp.setRoomSpace(classroom.getRoomSpace());
        classroomHelp.setDay(x);
        classroomHelp.setNumber(y);
        classroomHelp.setStatu(classroom.getStatu());
        classroomHelp.setMark(classroom.getMark());
        return classroomHelp;
    }
}
//    //建立一个课表二维数组，其中20 = 5天*一天4节课，后续可以把这个参数提取出去
//    //CourseTable[][] courseTables = new CourseTable[20][classrooms.size()];
//    HashMap<Integer, HashMap<Integer, List<Classroom>>> classroom = new HashMap<>(5);
//    HashMap<Integer, List<Classroom>> day01 = new HashMap<>(4);
//    HashMap<Integer, List<Classroom>> day02 = new HashMap<>(4);
//    HashMap<Integer, List<Classroom>> day03 = new HashMap<>(4);
//    HashMap<Integer, List<Classroom>> day04 = new HashMap<>(4);
//    HashMap<Integer, List<Classroom>> day05 = new HashMap<>(4);
//
//        classroom.put(1, day01);
//                classroom.put(2, day02);
//                classroom.put(3, day03);
//                classroom.put(4, day04);
//                classroom.put(5, day05);
//
//                for (int i = 1; i <= 4; i++) {
//                day01.put(i, classrooms);
//                day02.put(i, classrooms);
//                day03.put(i, classrooms);
//                day04.put(i, classrooms);
//                day05.put(i, classrooms);
//                }