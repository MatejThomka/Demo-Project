import React, {useState} from "react";
import {useRouter} from "next/router";
import Image from 'next/image';


const MonthlyExpenseButton = () => {
    const router = useRouter();
    const [isHovering, setIsHovering] = useState(false);
    const onMouseEnter = () => setIsHovering(true);
    const onMouseLeave = () => setIsHovering(false);
    const handleClick = () => {
        router.push('/monthly-expenses-page');
    }


    return (<div className="container mx-auto my-8">
        <div className="h-12"
             onMouseEnter={onMouseEnter}
             onMouseLeave={onMouseLeave}>
            <button
                onClick={handleClick}
                className="rounded bg-white text-black px-6 py-2 font-semibold w-[auto]">
                {isHovering ? (<Image className="bg-white"
                                      src="/pie-chart-exp.gif"
                                      width={256}
                                      height={256}
                                      alt="Picture of the monthly expense"/>) : (<Image className="bg-white"
                                                                                   src="/pie-chart-exp.png"
                                                                                   width={256}
                                                                                   height={256}
                                                                                   alt="Picture of the monthly expense"/>)}
                Monthly Expenses
            </button>
        </div>
    </div>)
}

export default MonthlyExpenseButton;