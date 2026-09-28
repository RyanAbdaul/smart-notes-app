import Buttons from "../common/Buttons";
import { LuStickyNote } from "react-icons/lu";
import { CiFolderOn } from "react-icons/ci";
import { FiTag } from "react-icons/fi";
import { IoTrashOutline } from "react-icons/io5";
const LibraryList = [
  { icon: <LuStickyNote />, value: "All Notes" },
  { icon: <CiFolderOn />, value: "Notebook" },
  { icon: <FiTag />, value: "Tags" },
  { icon: <IoTrashOutline />, value: "Trash" },
];
const Library = () => {
  return (
    <div className="flex items-start flex-col justify-start  ">
      <div className="text-xl w-full ml-1 text-zinc-500">Library</div>
      {LibraryList.map((ele, index) => (
        <Buttons key={index} icon={ele.icon} value={ele.value} />
      ))}
    </div>
  );
};

export default Library;
