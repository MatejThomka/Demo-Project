import React, {useState} from "react";
import {useRouter} from "next/router";
import Image from 'next/image';


const FinancialGoalButton = () => {
    const router = useRouter();
    const [isHovering, setIsHovering] = useState(false);
    const onMouseEnter = () => setIsHovering(true);
    const onMouseLeave = () => setIsHovering(false);
    const handleClick = () => {
        router.push('/incomes-&-expenses-page');
    }


    return (<div className="container mx-auto my-8">
        <div className="h-12"
             onMouseEnter={onMouseEnter}
             onMouseLeave={onMouseLeave}>
            <button
                onClick={handleClick}
                className="rounded bg-white text-black px-6 py-2 font-semibold w-[512]">
                {isHovering ? (<Image className="bg-white"
                                      src="/line-chart.gif"
                                      width={256}
                                      height={256}
                                      alt="Picture of Linegraph"/>) : (<Image className="bg-white"
                                                                              src="/line-chart.png"
                                                                              width={256}
                                                                              height={256}
                                                                              alt="Picture of Linegraph"/>)}
                Incomes & Expense
            </button>
        </div>
    </div>)
}

export default FinancialGoalButton;