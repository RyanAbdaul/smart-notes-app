import { useState } from "react";
import Search from "../common/Search";
import { IoFilterSharp } from "react-icons/io5";
import Buttons from "../common/Buttons";
import NoteCards from "../common/NoteCards";
import { useSelector } from "react-redux";
const orderedBy = ["All", "Pinned", "By Tag"];

const ListView = () => {
  const [activeTab, setActiveTab] = useState("All");
  const { data } = useSelector((state) => state.notes);

  return (
    <div className="w-full bg-[#ECEAEC] h-screen overflow-auto scrollbar-none [::-webkit-scrollbar]:hidden [-ms-overflow-style:none] ">
      {/* search && filter */}
      <div className="searchHeader flex ">
        <div className="flex-1 m-auto  p-2">
          <Search />
        </div>
        <div className="m-auto">
          <Buttons icon={<IoFilterSharp />} />
        </div>
      </div>
      {/*Order BY */}
      <div className="flex m-auto p-2 bg-zinc-200 rounded-xl">
        {orderedBy.map((ele, index) => (
          <button
            key={index}
            type="button"
            className={`p-1 m-auto w-full rounded-xl ${activeTab === ele ? "bg-white text-zinc-900 shadow-sm " : "text-zinc-500 hover:text-zinc-800"} `}
            onClick={() => setActiveTab(ele)}
          >
            {ele}
          </button>
        ))}
      </div>
      {data.length ? <NoteCards /> : <div className="text-center p-2 bg-white text-gray-400 rounded-xl m-2">There are no notes</div>}
    </div>
  );
};

export default ListView;
