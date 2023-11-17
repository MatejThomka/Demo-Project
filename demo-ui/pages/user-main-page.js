import Head from 'next/head';
import NavbarMainPage from "../components/navbars/NavbarMainPage";
import GenerateExpenseReport from "../components/buttons/GenerateExpenseReport";
import GenerateIncomeReport from "../components/buttons/GenerateIncomeReport";
import FinancialGoalButton from "../components/buttons/FinancialGoalButton";
import ExpenseIncomeButton from "../components/buttons/Expense&IncomeButton";
import MonthlyExpenseButton from "../components/buttons/MonthyExpenseButton";

export default function Home() {
    return (<div>
        <Head>
            <title>Demo of application</title>
        </Head>
        <NavbarMainPage/>
        <main>
            <h1 className="text-center text-gray-600 font-semibold text-2xl">Welcome to Demo of application
                Main page</h1>
            <div className="items-center flex px-20 place-content-center">
                <div className="flex px-2 items-center ">
                    <FinancialGoalButton/>
                </div>
                <div className="flex px-2 items-center">
                    <GenerateExpenseReport/>
                </div>
                <div className="flex px-2 items-center">
                    <GenerateIncomeReport/>
                </div>
                <div className="flex px-2 items-center">
                    <MonthlyExpenseButton/>
                </div>
                <div className="flex px-2 items-center">
                    <ExpenseIncomeButton/>
                </div>
            </div>


        </main>


    </div>)
}