import { readFileSync, writeFileSync } from 'fs';
import express, { type Request, type Response } from 'express';

const app = express();
app.use(express.json());

const adminPassword: string = 'admin123';  // 用于简单权限验证的管理员密码

interface Student {
    name: string;
    dutiesCount: number;
}

interface Duty {
    assigned: string | null;
    isDutyNeeded: boolean;
}

interface Duties {
    calendar: Record<string, Duty>;
    students: Student[];
}

let duties: Duties = {
    calendar: {},
    students: [
        { name: '张三', dutiesCount: 0 },
        { name: '李四', dutiesCount: 0 },
        { name: '王五', dutiesCount: 0 },
        { name: '田六', dutiesCount: 0 },
    ],
};

// 读取数据
function loadDuties(): void {
    try {
        const data = readFileSync('./duties.json', 'utf8');
        duties = JSON.parse(data) as Duties;
    } catch (err) {
        console.log("初次启动，未找到 duties.json 文件，创建新数据表");
        saveDuties()
    }
}

// 保存数据
function saveDuties(): void {
    writeFileSync('./duties.json', JSON.stringify(duties, null, 2));
}

// 初始化时加载数据
loadDuties();

// 工具函数：获取指定日期的格式 YYYY-MM-DD
function formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
}

// 自动分配给值日次数最少的同学
function assignDutyAutomatically(date: string): void {
    if (!duties.calendar[date]) {
        const minDutyCount = Math.min(...duties.students.map(student => student.dutiesCount));
        const leastAssignedStudents = duties.students.filter(student => student.dutiesCount === minDutyCount);
        const selectedStudent = leastAssignedStudents[Math.floor(Math.random() * leastAssignedStudents.length)];

        duties.calendar[date] = {
            assigned: selectedStudent.name,
            isDutyNeeded: true
        };
        selectedStudent.dutiesCount += 1;
        saveDuties();

        console.log(`[${date}] ${selectedStudent.name} 被自动分配为今日的值日生`);
    }
}

// API：查看所有学生的值日次数
app.get('/duty/all', (req: Request, res: Response) => {
    const studentsDutyCount = duties.students.map(student => ({
        name: student.name,
        dutiesCount: student.dutiesCount
    }));

    res.json(studentsDutyCount);
});

// API：查看某天是否需要值日以及值日生
app.get('/duty/:date', (req: Request, res: Response) => {
    const date: string = req.params.date;
    const dutyInfo = duties.calendar[date];

    if (dutyInfo) {
        res.json(dutyInfo);
    } else {
        duties.calendar[date] = {
            assigned: null,
            isDutyNeeded: true
        }
        res.json(duties.calendar[date]);
    }
});

// API：抢今日值日
app.post('/duty/today', (req: Request, res: Response) => {
    const today: string = formatDate(new Date());
    const { name }: { name: string } = req.body;

    let dutyInfo = duties.calendar[today]
    if (!dutyInfo) {
        duties.calendar[today] = {
            assigned: null,
            isDutyNeeded: true
        }
        dutyInfo = duties.calendar[today]
    }
    if (dutyInfo.assigned || !dutyInfo.isDutyNeeded) {
        return res.status(400).json(duties.calendar[today]);
    }

    const student = duties.students.find(student => student.name === name);
    if (!student) {
        console.log(`[${today}] 不存在名为 ${name} 的同学`);
        console.log(`[${today}] ${JSON.stringify(req.body, null, 2)}`);
        return res.status(400).json({ message: '该同学不存在' });
    }

    duties.calendar[today].assigned = name;
    student.dutiesCount += 1;
    saveDuties();

    res.json({ message: `${name} 抢到了今天的值日` });
    console.log(`[${formatDate(new Date())}] ${name} 抢到了值日`)
});

// API：管理员修改今日值日生
app.post('/duty/today/edit', (req: Request, res: Response) => {
    const today: string = formatDate(new Date());
    const { name, password }: { name: string; password: string } = req.body;

    if (password !== adminPassword) {
        return res.status(403).json({ message: '没有权限修改值日生' });
    }
    const previousStudent = duties.students.find(student => student.name == duties.calendar[today].assigned)
    const newStudent = duties.students.find(student => student.name === name);

    if (!newStudent) {
        return res.status(400).json({ message: '该同学不存在' });
    }

    if (previousStudent) {
        previousStudent.dutiesCount -= 1
    }
    newStudent.dutiesCount += 1
    duties.calendar[today].assigned = name;

    saveDuties();

    res.json({ message: `今天的值日生被修改为 ${name}` });
    console.log(`[${formatDate(new Date())}] 今天的值日生被修改为 ${name}`)
});

// API：管理员指定未来某天是否需要值日
app.post('/duty/set', (req: Request, res: Response) => {
    const { date, isDutyNeeded, password }: { date: string; isDutyNeeded: boolean; password: string; assigned: string } = req.body;

    if (password !== adminPassword) {
        return res.status(403).json({ message: '没有权限设置值日安排' });
    }

    duties.calendar[date] = {
        assigned: null,
        isDutyNeeded: isDutyNeeded
    };
    saveDuties();

    res.json({ message: `${date} 的值日安排已更新` });
    console.log(`[${date}] 值日安排已更新`)
});

// 每天早上八点自动检查当天是否需要分配值日
const now: Date = new Date();
let millisTill8AM: number = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 8, 0, 0, 0).getTime() - now.getTime();
if (millisTill8AM < 0) {
    millisTill8AM += 86400000; // 如果时间已经过了8点，则加一天
}

setTimeout(() => {
    const today: string = formatDate(new Date());
    if (!duties.calendar[today] || duties.calendar[today].isDutyNeeded) {
        assignDutyAutomatically(today);
    }
    setInterval(() => {
        const newToday: string = formatDate(new Date());
        if (!duties.calendar[newToday] || duties.calendar[newToday].isDutyNeeded) {
            assignDutyAutomatically(newToday);
        }
    }, 24 * 60 * 60 * 1000); // 之后每天检查
}, millisTill8AM);

// 启动服务器
app.listen(3000, () => {
    console.log('服务器在 http://localhost:3000 上运行');
});