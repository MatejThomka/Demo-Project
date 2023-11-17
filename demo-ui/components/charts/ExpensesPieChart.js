import {Pie} from "react-chartjs-2";
import {BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Title, Tooltip} from "chart.js";
import {useEffect, useState} from "react";
import {format} from "date-fns";

import "chart.js/auto";
import API_URLS_CONFIG from "../../API_URLS_CONFIG";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const ExpensesPieChart = () => {
    const EXPENSE_API_COUNT_ALL_URL = API_URLS_CONFIG.BASE_URL + "/api/expense/monthly";
    const [expenseData, setExpenseData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [monthOffset, setMonthOffset] = useState(0);
    const [monthName, setMonthName] = useState("");

    const EXPENSE_CURRENT_API_URL = API_URLS_CONFIG.BASE_URL + "/api/expense/previous-and-current-month";
    const [expenseDataCR, setExpenseDataCR] = useState([]);
    const [loadingCR, setLoadingCR] = useState(true);
    const [monthType, setMonthType] = useState("")
    const currentDate = new Date();
    const currentMonthName = format(currentDate, "MMMM");
    const [monthNameCR, setMonthNameCR] = useState(currentMonthName);


    const handleChangeMonth = (event) => {
        const selectedMonth = parseInt(event.target.value);
        setMonthOffset(selectedMonth);
        const monthNames = ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
        setMonthName(monthNames[selectedMonth]);
    };

    const handleChangeMonthType = (event) => {
        const selectedMonthType = event.target.value;
        setMonthType(selectedMonthType);

        const currentDate = new Date();
        const currentMonthName = format(currentDate, "MMMM");
        const previousMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() - 1);
        const previousMonthName = format(previousMonth, "MMMM");

        if (selectedMonthType === "current") {
            setMonthNameCR(currentMonthName);
        } else if (selectedMonthType === "previous") {
            setMonthNameCR(previousMonthName);
        }

    };

    useEffect(() => {
        setLoading(true);
        fetch(`${EXPENSE_API_COUNT_ALL_URL}?monthOffset=${monthOffset}`, {
            method: "GET",
            headers: {
                Accept: "application/json",
                Authorization: "Bearer " + localStorage.getItem("token"),
            },
        })
            .then((res) => res.json())
            .then((data) => {
                console.log(data);
                setExpenseData(data);
                setLoading(false);
            })
            .catch((error) => {
                console.log(error);

            });
    }, [monthOffset]);

    useEffect(() => {
        setLoadingCR(true);
        fetch(`${EXPENSE_CURRENT_API_URL}?monthType=${monthType}`, {
            method: "GET",
            headers: {
                Accept: "application/json",
                Authorization: "Bearer " + localStorage.getItem("token"),
            },
        })
            .then((res) => res.json())
            .then((data) => {
                console.log(data);
                setExpenseDataCR(data);
                setLoadingCR(false);

            })
            .catch((error) => {
                console.log(error);
            });
    }, [monthType]);


    const chartData = {
        labels: expenseData?.map((x) => x.category),
        datasets: [
            {
                labels: [monthName],
                data: expenseData.map((y) => y.expenses),
                backgroundColor: [
                    "rgba(255, 99, 132, 0.6)",
                    "rgba(54, 162, 235, 0.6)",
                    "rgba(255, 206, 86, 0.6)",
                    "rgba(75, 192, 192, 0.6)",
                    "rgba(153, 102, 255, 0.6)",
                ],
            },
        ],
    };

    const options = {
        responsive: true,
        plugins: {
            legend: {
                position: "top"
            },
            title: {
                display: true,
                text: `Expenses for ${monthName}`,
            },
        },
        data: expenseData,
    };

    const chartDataCR = {
        labels: expenseDataCR?.map((x) => x.category),
        datasets: [
            {
                labels: [monthNameCR],
                data: expenseDataCR.map((y) => y.expenses),
                backgroundColor: [
                    "rgba(255, 99, 132, 0.6)",
                    "rgba(54, 162, 235, 0.6)",
                    "rgba(255, 206, 86, 0.6)",
                    "rgba(75, 192, 192, 0.6)",
                    "rgba(153, 102, 255, 0.6)",
                ],
            },
        ],
        data: expenseDataCR,
    };

    const optionsCR = {
        responsive: true,
        plugins: {
            legend: {
                position: "top"
            },
            title: {
                display: true,
                text: `Expenses for ${monthNameCR}`,
            },
        },
        data: expenseDataCR,
    };


    return (
        <div className="items-center flex px-20">
            <div className="w-[50%] float: left">
                <div>
                    <label htmlFor="monthSelect">Select Month:</label>
                    <select id="monthSelect" value={monthOffset} onChange={handleChangeMonth} style={{
                        width: '200px',
                        padding: '8px',
                        fontSize: '16px',
                        border: '1px solid #ccc',
                        borderRadius: '4px',
                        outline: 'none',
                        backgroundColor: '#fff',
                        color: '#333',
                        cursor: 'pointer'
                    }}>
                        <option value={0}>Choose...</option>
                        <option value={1}>January</option>
                        <option value={2}>February</option>
                        <option value={3}>March</option>
                        <option value={4}>April</option>
                        <option value={5}>May</option>
                        <option value={6}>June</option>
                        <option value={7}>July</option>
                        <option value={8}>August</option>
                        <option value={9}>September</option>
                        <option value={10}>October</option>
                        <option value={11}>November</option>
                        <option value={12}>December</option>
                    </select>
                </div>
                <div className="max-w-[1600x] h-screen flex justify-between">
                    <div className="border border-gray-400 pt-0 rounded-xl w-full h-fit my-auto shadow-xl">
                        {!loading && monthOffset !== 0 ? (
                            <div>
                                <Pie data={chartData} options={options}/>
                            </div>
                        ) : (
                            <p>Please select a month.</p>
                        )}
                    </div>
                </div>
            </div>
            <div className="w-[50%] float: right">
                <div>
                    <div>
                        <label htmlFor="monthSelectCR">Select Current/Previous:</label>
                        <select id="monthSelectCR" value={monthType} onChange={handleChangeMonthType} style={{
                            width: '200px',
                            padding: '8px',
                            fontSize: '16px',
                            border: '1px solid #ccc',
                            borderRadius: '4px',
                            outline: 'none',
                            backgroundColor: '#fff',
                            color: '#333',
                            cursor: 'pointer'
                        }}>
                            <option value={""}>Choose...</option>
                            <option value={"current"}>Current Month</option>
                            <option value={"previous"}>Previous Month</option>
                        </select>
                    </div>
                </div>
                <div className="max-w-[1600x] h-screen flex mx-auto my-auto">
                    <div className="border border-gray-400 pt-0 rounded-xl w-full h-fit my-auto shadow-xl">
                        {!loadingCR && monthType !== "" ? (
                            <div>
                                <Pie data={chartDataCR} options={optionsCR}/>
                            </div>
                        ) : (
                            <p>Please select Current or Previous Month.</p>
                        )}
                    </div>
                </div>
            </div>
        </div>

    );
};

export default ExpensesPieChart;