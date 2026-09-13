import React from "react";
import { FaSearch } from "react-icons/fa";

const Search = () => {
  return (
    <div className="search flex items-center w-full ">
      <span className="bg-[#F1EEF0] rounded-xl rounded-r-none p-4 ">
        <FaSearch />
      </span>
      <input
        type="search"
        className="bg-[#F1EEF0] rounded-xl w-full p-3 mr-4 rounded-l-none outline-none"
        placeholder="Search notes ,tags and folders"
      />
    </div>
  );
};

export default Search;
