import Head from 'next/head';
import NavbarOtherPages from "../components/navbars/NavbarOtherPages";
import ExpenseIncomeLineChart from "../components/charts/Expense&IncomeLineChart";


export default function Home() {
    return (<div>
        <Head>
            <title>Demo of application</title>
        </Head>
        <NavbarOtherPages/>
        <main>
            <h1 className="text-center text-gray-600 font-semibold text-2xl">
                Graphical Showtime of your Incomes and Expense through months
            </h1>
            <div>
                <ExpenseIncomeLineChart/>
            </div>

        </main>


    </div>)
}