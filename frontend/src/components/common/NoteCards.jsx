import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import Buttons from "./Buttons";
import { setActiveNote } from "../../features/notes/notesSlice";
import ErrorHandler from "../../utils/ErrorHandler";
const NoteCards = () => {
  
  const [maxData, setMaxCount] = useState(5);
  const { data, error } = useSelector((state) => state.notes);
  const dispatch = useDispatch();
  return (
    <div>
      {error && <ErrorHandler error={error}/>}
      {data.slice(0, maxData).map((ele) => (
        <div
          onClick={() => dispatch(setActiveNote(ele.id))}
          key={ele.id}
          className="bg-[#F4F1F3] hover:cursor-pointer shadow-sm m-2 p-2 rounded-xl h-full"
        >
          <h3 dir="auto" className="text-2xl font-medium truncate m-1">
            {ele.title}
          </h3>
          <p dir="auto" className="truncate m-1 text-gray-500">
            {ele.description}
          </p>
          <span
            dir="auto"
            className="bg-[#EAE7EA] inline-block py-1 px-2 my-1 font-semibold rounded-xl hover:cursor-pointer"
          >
            {ele.tag || "#react"}
          </span>
        </div>
      ))}

      {maxData < data.length && (
        <Buttons
          onClick={() =>
            maxData < data.length
              ? setMaxCount((prev) => prev + 5)
              : setMaxCount(15)
          }
          value={maxData < data.length ? "Show more" : "Close"}
        />
      )}
    </div>
  );
};

export default NoteCards;
